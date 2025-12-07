using FarmaciaApi.Data;
using FarmaciaApi.DTOs;
using FarmaciaApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PedidosController : ControllerBase
    {
        private readonly FarmaciaContext _context;

        public PedidosController(FarmaciaContext context)
        {
            _context = context;
        }

        // POST: api/pedidos
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CreatePedidoDto dto)
        {
            if (!ModelState.IsValid) return ValidationProblem(ModelState);

            var ids = dto.Items.Select(i => i.MedicamentoId).Distinct().ToList();

            var meds = await _context.Medicamentos
                .Where(m => ids.Contains(m.Id))
                .ToListAsync();

            if (meds.Count != ids.Count)
                return BadRequest(new { message = "Algún medicamento no existe." });

            foreach (var item in dto.Items)
            {
                var med = meds.First(m => m.Id == item.MedicamentoId);
                if (item.Cantidad <= 0)
                    return BadRequest(new { message = "Cantidad inválida." });

                if (med.Cantidad < item.Cantidad)
                    return BadRequest(new
                    {
                        message = $"No hay stock suficiente de {med.Nombre}. Disponible: {med.Cantidad}"
                    });
            }

            var pedido = new Pedido
            {
                ClienteNombre = dto.ClienteNombre,
                DireccionEntrega = dto.DireccionEntrega,        // 🔹 nuevo
                CoordenadasEntrega = dto.CoordenadasEntrega,    // 🔹 nuevo
                CostoEnvio = dto.CostoEnvio,                    // 🔹 nuevo
                FechaCreacion = DateTime.UtcNow,
                Estado = "Pendiente"
            };

            decimal totalProductos = 0m;
            var detalles = new List<PedidoDetalle>();

            foreach (var item in dto.Items)
            {
                var med = meds.First(m => m.Id == item.MedicamentoId);
                var subtotal = med.Precio * item.Cantidad;
                totalProductos += subtotal;

                detalles.Add(new PedidoDetalle
                {
                    MedicamentoId = med.Id,
                    Medicamento = med,
                    Cantidad = item.Cantidad,
                    PrecioUnitario = med.Precio,
                    Subtotal = subtotal,
                    MedicamentoNombre = med.Nombre
                });

                // Descontamos stock
                med.Cantidad -= item.Cantidad;
            }

            // Total = productos + costo de envío
            pedido.Total = totalProductos + pedido.CostoEnvio;
            pedido.Detalles = detalles;

            _context.Pedidos.Add(pedido);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetOne), new { id = pedido.Id }, new
            {
                pedido.Id,
                pedido.ClienteNombre,
                pedido.DireccionEntrega,
                pedido.CoordenadasEntrega,
                pedido.CostoEnvio,
                pedido.FechaCreacion,
                pedido.Estado,
                pedido.Total
            });
        }

        // GET: api/pedidos
        [HttpGet]
        public async Task<IActionResult> GetAll([FromQuery] string? estado = null)
        {
            var query = _context.Pedidos
                .Include(p => p.Detalles)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(estado))
            {
                query = query.Where(p => p.Estado == estado);
            }

            var lista = await query
                .OrderByDescending(p => p.FechaCreacion)
                .Select(p => new
                {
                    p.Id,
                    p.ClienteNombre,
                    p.DireccionEntrega,      // 🔹 nuevo
                    p.CoordenadasEntrega,    // 🔹 nuevo
                    p.CostoEnvio,            // 🔹 nuevo
                    p.FechaCreacion,
                    p.Estado,
                    p.Total,
                    Detalles = p.Detalles.Select(d => new
                    {
                        d.MedicamentoId,
                        d.MedicamentoNombre,
                        d.Cantidad,
                        d.PrecioUnitario,
                        d.Subtotal
                    }).ToList()
                })
                .ToListAsync();

            return Ok(lista);
        }

        // DELETE: api/pedidos/5
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var pedido = await _context.Pedidos
                .Include(p => p.Detalles)
                .ThenInclude(d => d.Medicamento)
                .FirstOrDefaultAsync(p => p.Id == id);

            if (pedido == null)
                return NotFound(new { message = "El pedido no existe." });

            // 🔹 Si el pedido NO estaba cancelado/rechazado, regresamos stock
            if (pedido.Estado != "Cancelado" && pedido.Estado != "Rechazado")
            {
                foreach (var det in pedido.Detalles)
                {
                    if (det.Medicamento != null)
                    {
                        det.Medicamento.Cantidad += det.Cantidad;
                    }
                    else
                    {
                        var med = await _context.Medicamentos.FindAsync(det.MedicamentoId);
                        if (med != null)
                            med.Cantidad += det.Cantidad;
                    }
                }
            }

            // 🔥 Eliminación real
            _context.PedidoDetalles.RemoveRange(pedido.Detalles);
            _context.Pedidos.Remove(pedido);

            await _context.SaveChangesAsync();

            return Ok(new { message = "Pedido eliminado permanentemente." });
        }

        // GET: api/pedidos/5
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetOne(int id)
        {
            var p = await _context.Pedidos
                .Include(p => p.Detalles)
                .FirstOrDefaultAsync(p => p.Id == id);

            if (p == null) return NotFound();

            return Ok(new
            {
                p.Id,
                p.ClienteNombre,
                p.DireccionEntrega,      // 🔹 nuevo
                p.CoordenadasEntrega,    // 🔹 nuevo
                p.CostoEnvio,            // 🔹 nuevo
                p.FechaCreacion,
                p.Estado,
                p.Total,
                Detalles = p.Detalles.Select(d => new
                {
                    d.MedicamentoId,
                    d.MedicamentoNombre,
                    d.Cantidad,
                    d.PrecioUnitario,
                    d.Subtotal
                }).ToList()
            });
        }

        // PATCH: api/pedidos/5/estado?estado=Cancelado
        [HttpPatch("{id:int}/estado")]
        public async Task<IActionResult> CambiarEstado(int id, [FromQuery] string estado)
        {
            if (string.IsNullOrWhiteSpace(estado))
                return BadRequest(new { message = "Estado inválido." });

            // Cargamos el pedido con detalles y medicamentos
            var pedido = await _context.Pedidos
                .Include(p => p.Detalles)
                .ThenInclude(d => d.Medicamento)
                .FirstOrDefaultAsync(p => p.Id == id);

            if (pedido == null)
                return NotFound();

            var estadoAnterior = pedido.Estado;
            pedido.Estado = estado;

            // Si pasa a Cancelado o Rechazado y antes NO lo estaba,
            // devolvemos el stock al inventario
            if ((estado == "Cancelado" || estado == "Rechazado") &&
                estadoAnterior != "Cancelado" && estadoAnterior != "Rechazado")
            {
                foreach (var det in pedido.Detalles)
                {
                    if (det.Medicamento != null)
                    {
                        det.Medicamento.Cantidad += det.Cantidad;
                    }
                    else
                    {
                        var med = await _context.Medicamentos.FindAsync(det.MedicamentoId);
                        if (med != null)
                        {
                            med.Cantidad += det.Cantidad;
                        }
                    }
                }
            }

            await _context.SaveChangesAsync();

            return Ok(new { pedido.Id, pedido.Estado });
        }

        // GET: api/pedidos/dashboard
        [HttpGet("dashboard")]
        public async Task<IActionResult> GetDashboard()
        {
            var pedidos = await _context.Pedidos
                .Include(p => p.Detalles)
                .ThenInclude(d => d.Medicamento)
                .ToListAsync();

            if (pedidos == null || !pedidos.Any())
            {
                return NotFound(new { message = "No hay pedidos registrados." });
            }

            // Diccionario: MedicamentoId -> cantidad total vendida
            var ventas = new Dictionary<int, int>();

            foreach (var pedido in pedidos)
            {
                foreach (var detalle in pedido.Detalles)
                {
                    if (ventas.ContainsKey(detalle.MedicamentoId))
                    {
                        ventas[detalle.MedicamentoId] += detalle.Cantidad;
                    }
                    else
                    {
                        ventas[detalle.MedicamentoId] = detalle.Cantidad;
                    }
                }
            }

            // Cargamos todos los medicamentos usados en ventas
            var idsMeds = ventas.Keys.ToList();

            var meds = await _context.Medicamentos
                .Where(m => idsMeds.Contains(m.Id))
                .ToListAsync();

            // Top y bottom
            var medicamentoMasVendido = ventas
                .OrderByDescending(v => v.Value)
                .FirstOrDefault();

            var medicamentoMenosVendido = ventas
                .OrderBy(v => v.Value)
                .FirstOrDefault();

            var medicMasVendidoNombre = meds
                .Where(m => m.Id == medicamentoMasVendido.Key)
                .Select(m => m.Nombre)
                .FirstOrDefault();

            var medicMenosVendidoNombre = meds
                .Where(m => m.Id == medicamentoMenosVendido.Key)
                .Select(m => m.Nombre)
                .FirstOrDefault();

            // Lista completa para la gráfica (ordenada de mayor a menor)
            var ventasPorMedicamento = meds
                .Select(m => new
                {
                    id = m.Id,
                    nombre = m.Nombre,
                    cantidad = ventas.ContainsKey(m.Id) ? ventas[m.Id] : 0
                })
                .OrderByDescending(x => x.cantidad)
                .ToList();

            var dashboard = new
            {
                medicamentoMasVendido = new
                {
                    id = medicamentoMasVendido.Key,
                    nombre = medicMasVendidoNombre,
                    cantidad = medicamentoMasVendido.Value
                },
                medicamentoMenosVendido = new
                {
                    id = medicamentoMenosVendido.Key,
                    nombre = medicMenosVendidoNombre,
                    cantidad = medicamentoMenosVendido.Value
                },
                ventasPorMedicamento // 🔹 para la gráfica general
            };

            return Ok(dashboard);
        }
    }
}
