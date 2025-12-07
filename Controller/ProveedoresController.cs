using FarmaciaApi.Data;
using FarmaciaApi.Dtos;
using FarmaciaApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;

namespace FarmaciaApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProveedoresController : ControllerBase
    {
        private readonly FarmaciaContext _context;

        public ProveedoresController(FarmaciaContext context)
        {
            _context = context;
        }

        // ================== CRUD NORMAL DE PROVEEDORES ==================

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var lista = await _context.Proveedores
                .OrderBy(p => p.Nombre)
                .Select(p => new
                {
                    p.Id,
                    p.Nombre,
                    p.Contacto,
                    p.Telefono,
                    p.Email,
                    p.Direccion,
                    p.Rfc,
                    p.Activo,
                    p.FechaCreacion
                })
                .ToListAsync();

            return Ok(lista);
        }

        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetOne(int id)
        {
            var p = await _context.Proveedores.FindAsync(id);
            if (p == null) return NotFound();

            return Ok(p);
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CreateProveedorDto dto)
        {
            if (!ModelState.IsValid)
                return ValidationProblem(ModelState);

            var proveedor = new Proveedor
            {
                Nombre = dto.Nombre,
                Contacto = dto.Contacto,
                Telefono = dto.Telefono,
                Email = dto.Email,
                Direccion = dto.Direccion,
                Rfc = dto.Rfc,
                Activo = true
            };

            _context.Proveedores.Add(proveedor);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetOne), new { id = proveedor.Id }, proveedor);
        }

        [HttpPut("{id:int}")]
        public async Task<IActionResult> Update(int id, [FromBody] UpdateProveedorDto dto)
        {
            if (!ModelState.IsValid)
                return ValidationProblem(ModelState);

            var proveedor = await _context.Proveedores.FindAsync(id);
            if (proveedor == null) return NotFound();

            proveedor.Nombre = dto.Nombre;
            proveedor.Contacto = dto.Contacto;
            proveedor.Telefono = dto.Telefono;
            proveedor.Email = dto.Email;
            proveedor.Direccion = dto.Direccion;
            proveedor.Rfc = dto.Rfc;
            proveedor.Activo = dto.Activo;

            await _context.SaveChangesAsync();
            return Ok(proveedor);
        }

        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var proveedor = await _context.Proveedores.FindAsync(id);
            if (proveedor == null) return NotFound();

            _context.Proveedores.Remove(proveedor);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        [HttpPatch("{id:int}/toggle")]
        public async Task<IActionResult> Toggle(int id)
        {
            var proveedor = await _context.Proveedores.FindAsync(id);
            if (proveedor == null) return NotFound();

            proveedor.Activo = !proveedor.Activo;
            await _context.SaveChangesAsync();

            return Ok(new { proveedor.Id, proveedor.Activo });
        }

        // ================== HISTORIAL DE PEDIDOS EMBEBIDO ==================

        // GET: api/Proveedores/5/pedidos-historial
        [HttpGet("{id:int}/pedidos-historial")]
        public async Task<IActionResult> GetPedidosHistorial(int id)
        {
            var proveedor = await _context.Proveedores.FindAsync(id);
            if (proveedor == null) return NotFound();

            if (string.IsNullOrWhiteSpace(proveedor.HistorialPedidosJson))
                return Ok(new List<PedidoProveedorHistDto>());

            try
            {
                var lista = JsonSerializer.Deserialize<List<PedidoProveedorHistDto>>(
                    proveedor.HistorialPedidosJson
                ) ?? new List<PedidoProveedorHistDto>();

                return Ok(lista);
            }
            catch
            {
                // si por alguna razón el JSON está roto, regreso lista vacía
                return Ok(new List<PedidoProveedorHistDto>());
            }
        }

        // POST: api/Proveedores/5/pedidos-historial
        [HttpPost("{id:int}/pedidos-historial")]
        public async Task<IActionResult> AddPedidoHistorial(
            int id,
            [FromBody] PedidoProveedorHistDto dto)
        {
            var proveedor = await _context.Proveedores.FindAsync(id);
            if (proveedor == null) return NotFound();

            if (dto.Items == null || !dto.Items.Any())
                return BadRequest("El pedido debe tener al menos un item.");

            // Recalcular totales en el backend por seguridad
            dto.TotalExistentes = 0;
            dto.TotalNuevos = 0;
            foreach (var item in dto.Items)
            {
                item.Subtotal = item.Cantidad * item.Precio;
                if (item.Origen == "Existente")
                    dto.TotalExistentes += item.Subtotal;
                else
                    dto.TotalNuevos += item.Subtotal;
            }
            dto.TotalGeneral = dto.TotalExistentes + dto.TotalNuevos;
            dto.Fecha = DateTime.UtcNow;

            // Leer historial actual del proveedor
            List<PedidoProveedorHistDto> lista;
            if (string.IsNullOrWhiteSpace(proveedor.HistorialPedidosJson))
            {
                lista = new List<PedidoProveedorHistDto>();
            }
            else
            {
                try
                {
                    lista = JsonSerializer.Deserialize<List<PedidoProveedorHistDto>>(
                        proveedor.HistorialPedidosJson
                    ) ?? new List<PedidoProveedorHistDto>();
                }
                catch
                {
                    lista = new List<PedidoProveedorHistDto>();
                }
            }

            // Asignar Id incremental dentro del proveedor
            dto.Id = lista.Count == 0 ? 1 : lista.Max(p => p.Id) + 1;

            lista.Add(dto);

            proveedor.HistorialPedidosJson = JsonSerializer.Serialize(lista);
            await _context.SaveChangesAsync();

            return Ok(dto); // regreso el pedido ya guardado con Id, totales y fecha
        }
    }

    // =============== DTOs internos solo para historial ===============

    public class PedidoProveedorItemHistDto
    {
        public string Origen { get; set; } = "";              // "Existente" | "Nuevo"
        public string NombreMedicamento { get; set; } = "";
        public string? Tipo { get; set; }
        public int Cantidad { get; set; }
        public decimal Precio { get; set; }
        public decimal Subtotal { get; set; }
    }

    public class PedidoProveedorHistDto
    {
        public int Id { get; set; }              // Id interno del pedido (por proveedor)
        public DateTime Fecha { get; set; }

        public string? SolicitadoPor { get; set; }
        public string? Notas { get; set; }

        public decimal TotalExistentes { get; set; }
        public decimal TotalNuevos { get; set; }
        public decimal TotalGeneral { get; set; }

        public List<PedidoProveedorItemHistDto> Items { get; set; } = new();
    }
}
