using FarmaciaApi.Data;
using FarmaciaApi.Dtos;
using FarmaciaApi.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    [AllowAnonymous]
    public class CitasController : ControllerBase
    {
        private readonly FarmaciaContext _db;
        public CitasController(FarmaciaContext db) => _db = db;

        // 🔹 GET: api/citas/slots?dia=2025-11-25
        [HttpGet("slots")]
        public async Task<IActionResult> GetSlots([FromQuery] DateTime? dia)
        {
            var fecha = (dia ?? DateTime.Today).Date;

            var inicio = TimeSpan.FromHours(9);
            var fin = TimeSpan.FromHours(18);
            var paso = TimeSpan.FromMinutes(30);

            var ocupadas = await _db.Citas
                .Where(c => c.FechaHora.Date == fecha && c.Estatus == "A")
                .Select(c => c.FechaHora.TimeOfDay)
                .ToListAsync();

            var slots = new List<object>();

            for (var t = inicio; t < fin; t += paso)
            {
                var dt = fecha.Add(t);

                slots.Add(new
                {
                    fechaHora = dt,
                    horaTexto = dt.ToString("HH:mm"),
                    disponible = !ocupadas.Contains(t)
                });
            }

            return Ok(slots);
        }

        // 🔹 POST: api/citas
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CitaCreateDto dto)
        {
            // Normalizamos a minutos (segundos en 0)
            var fh = new DateTime(
                dto.FechaHora.Year,
                dto.FechaHora.Month,
                dto.FechaHora.Day,
                dto.FechaHora.Hour,
                dto.FechaHora.Minute,
                0,
                DateTimeKind.Local
            );

            if (fh.TimeOfDay < TimeSpan.FromHours(9) || fh.TimeOfDay >= TimeSpan.FromHours(18))
                return BadRequest("La hora debe estar entre 09:00 y 18:00.");

            var ocupado = await _db.Citas.AnyAsync(c => c.Estatus == "A" && c.FechaHora == fh);
            if (ocupado) return Conflict("Ese horario ya está ocupado.");

            var cita = new Cita
            {
                FechaHora = fh,
                TipoConsulta = dto.TipoConsulta,
                Notas = dto.Notas,
                DuracionMin = dto.DuracionMin ?? 30,
                Estatus = "A",

                NombrePaciente = dto.NombrePaciente,

                // 🔹 Nuevos campos
                Observaciones = dto.Observaciones,
                Diagnostico = dto.Diagnostico,
                Medicamentos = dto.Medicamentos
            };

            _db.Citas.Add(cita);
            await _db.SaveChangesAsync();

            return CreatedAtAction(nameof(GetById), new { id = cita.IdCita }, cita);
        }

        // 🔹 PUT: api/citas/5
        [HttpPut("{id:int}")]
        public async Task<IActionResult> Update(int id, [FromBody] CitaUpdateDto dto)
        {
            var cita = await _db.Citas.FirstOrDefaultAsync(c => c.IdCita == id);
            if (cita is null) return NotFound();

            cita.TipoConsulta = dto.TipoConsulta;
            cita.Notas = dto.Notas;

            // 🔹 Nuevos campos
            cita.Observaciones = dto.Observaciones;
            cita.Diagnostico = dto.Diagnostico;
            cita.Medicamentos = dto.Medicamentos;

            if (!string.IsNullOrWhiteSpace(dto.Estatus))
            {
                var e = dto.Estatus.Trim().ToUpperInvariant();
                if (e != "A" && e != "C" && e != "T")
                    return BadRequest("Estatus inválido (usa 'A', 'C' o 'T').");
                cita.Estatus = e;
            }

            await _db.SaveChangesAsync();
            return NoContent();
        }

        // 🔹 GET: api/citas
        [HttpGet]
        public async Task<IActionResult> GetMine(
            [FromQuery] DateTime? desde,
            [FromQuery] DateTime? hasta,
            [FromQuery] string estado = "Todos")
        {
            var q = _db.Citas.AsNoTracking();

            if (estado != "Todos")
            {
                q = q.Where(c => c.Estatus == estado);
            }

            if (desde.HasValue) q = q.Where(c => c.FechaHora >= desde.Value);
            if (hasta.HasValue) q = q.Where(c => c.FechaHora < hasta.Value);

            var lista = await q
                .OrderBy(c => c.FechaHora)
                .ToListAsync();

            return Ok(lista);
        }

        // 🔹 GET: api/citas/5
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetById(int id)
        {
            var cita = await _db.Citas.AsNoTracking()
                .FirstOrDefaultAsync(c => c.IdCita == id);

            return cita is null ? NotFound() : Ok(cita);
        }

        // 🔹 DELETE lógico: api/citas/5  (Cancelación)
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Cancel(int id)
        {
            var cita = await _db.Citas.FirstOrDefaultAsync(c => c.IdCita == id);
            if (cita is null) return NotFound();

            cita.Estatus = "C";
            await _db.SaveChangesAsync();
            return NoContent();
        }

        // 🔥 DELETE FÍSICO: api/citas/5/real
        [HttpDelete("{id:int}/real")]
        public async Task<IActionResult> DeleteReal(int id)
        {
            var cita = await _db.Citas.FirstOrDefaultAsync(c => c.IdCita == id);
            if (cita is null)
                return NotFound("La cita no existe.");

            _db.Citas.Remove(cita);
            await _db.SaveChangesAsync();

            return Ok("Cita eliminada permanentemente.");
        }
    }
}
