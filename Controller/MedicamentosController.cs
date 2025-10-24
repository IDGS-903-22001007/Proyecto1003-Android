using FarmaciaApi.Data;
using FarmaciaApi.DTOs;
using FarmaciaApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MedicamentosController : ControllerBase
    {
        private readonly FarmaciaContext _context;

        public MedicamentosController(FarmaciaContext context)
        {
            _context = context;
        }

        // GET: api/Medicamentos
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var lista = await _context.Medicamentos
                .OrderByDescending(m => m.FechaCreacion)
                .Select(m => new
                {
                    m.Id,
                    m.Nombre,
                    m.Cantidad,
                    m.Tipo,
                    m.Precio,
                    m.Descripcion,
                    m.Beneficios,
                    m.Instrucciones,
                    m.Advertencias,
                    m.FotoUrl,
                    m.Activo,
                    m.FechaCreacion
                })
                .ToListAsync();

            return Ok(lista);
        }

        // GET: api/Medicamentos/5
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetOne(int id)
        {
            var m = await _context.Medicamentos.FindAsync(id);
            if (m == null) return NotFound();
            return Ok(m);
        }

        // POST: api/Medicamentos  (JSON)
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CreateMedicamentoDto dto)
        {
            if (!ModelState.IsValid) return ValidationProblem(ModelState);

            var med = new Medicamento
            {
                Nombre = dto.Nombre,
                Cantidad = dto.Cantidad,
                Descripcion = dto.Descripcion,
                Beneficios = dto.Beneficios,
                Instrucciones = dto.Instrucciones,
                Advertencias = dto.Advertencias,
                Tipo = dto.Tipo,
                Precio = dto.Precio,
                FotoUrl = dto.FotoUrl,
                Activo = true
            };

            _context.Medicamentos.Add(med);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetOne), new { id = med.Id }, med);
        }

        // PUT: api/Medicamentos/5  (JSON)
        [HttpPut("{id:int}")]
        public async Task<IActionResult> Update(int id, [FromBody] UpdateMedicamentoDto dto)
        {
            if (!ModelState.IsValid) return ValidationProblem(ModelState);

            var med = await _context.Medicamentos.FindAsync(id);
            if (med == null) return NotFound();

            med.Nombre = dto.Nombre;
            med.Cantidad = dto.Cantidad;
            med.Descripcion = dto.Descripcion;
            med.Beneficios = dto.Beneficios;
            med.Instrucciones = dto.Instrucciones;
            med.Advertencias = dto.Advertencias;
            med.Tipo = dto.Tipo;
            med.Precio = dto.Precio;
            med.FotoUrl = dto.FotoUrl;

            await _context.SaveChangesAsync();
            return Ok(med);
        }

        // DELETE: api/Medicamentos/5
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var med = await _context.Medicamentos.FindAsync(id);
            if (med == null) return NotFound();

            _context.Medicamentos.Remove(med);
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // PATCH: api/Medicamentos/5/toggle
        [HttpPatch("{id:int}/toggle")]
        public async Task<IActionResult> Toggle(int id)
        {
            var med = await _context.Medicamentos.FindAsync(id);
            if (med == null) return NotFound();

            med.Activo = !med.Activo;
            await _context.SaveChangesAsync();
            return Ok(new { med.Id, med.Activo });
        }
    }
}
