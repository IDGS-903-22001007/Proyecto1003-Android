using FarmaciaApi.Data;
using FarmaciaApi.Models;
using FarmaciaApi.Security; // <— AÑADE ESTO
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Linq;
using System.Threading.Tasks;

namespace FarmaciaApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuariosController : ControllerBase
    {
        private readonly FarmaciaContext _context;

        public UsuariosController(FarmaciaContext context) => _context = context;

        // GET: api/usuarios
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var lista = await _context.Usuarios
                .Select(u => new {
                    u.Id,
                    u.Nombre,
                    u.Apellido,
                    u.Telefono,
                    u.Correo,
                    u.User,
                    u.Direccion,
                    u.Rol,
                    u.Activo,
                    u.FechaCreacion
                })
                .ToListAsync();

            return Ok(lista);
        }

        // GET: api/usuarios/5
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetOne(int id)
        {
            var u = await _context.Usuarios.FindAsync(id);
            if (u == null) return NotFound();

            return Ok(new
            {
                u.Id,
                u.Nombre,
                u.Apellido,
                u.Telefono,
                u.Correo,
                u.User,
                u.Direccion,
                u.Rol,
                u.Activo,
                u.FechaCreacion
            });
        }

        // POST: api/usuarios
        [HttpPost]
        public async Task<IActionResult> Create([FromBody] UsuarioCreateDto dto)
        {
            if (await _context.Usuarios.AnyAsync(x => x.User == dto.Usuario))
                return Conflict("El nombre de usuario ya existe.");
            if (await _context.Usuarios.AnyAsync(x => x.Correo == dto.Correo))
                return Conflict("El correo ya está registrado.");

            var entity = new Usuario
            {
                Nombre = dto.Nombre,
                Apellido = dto.Apellido,
                Telefono = dto.Telefono,
                Correo = dto.Correo,
                User = dto.Usuario,
                Direccion = dto.Direccion,
                ContrasenaHash = PasswordHasherUtil.Hash(dto.Contrasena), // <— AQUI
                Rol = string.IsNullOrWhiteSpace(dto.Rol) ? "user" : dto.Rol,
                Activo = true
            };

            _context.Usuarios.Add(entity);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetOne), new { id = entity.Id }, new
            {
                entity.Id,
                entity.Nombre,
                entity.Apellido,
                entity.User,
                entity.Correo,
                entity.Rol
            });
        }

        // PUT: api/usuarios/5
        [HttpPut("{id:int}")]
        public async Task<IActionResult> Update(int id, [FromBody] UsuarioUpdateDto dto)
        {
            var u = await _context.Usuarios.FindAsync(id);
            if (u == null) return NotFound();

            if (u.User != dto.Usuario && await _context.Usuarios.AnyAsync(x => x.User == dto.Usuario))
                return Conflict("El nombre de usuario ya existe.");
            if (u.Correo != dto.Correo && await _context.Usuarios.AnyAsync(x => x.Correo == dto.Correo))
                return Conflict("El correo ya está registrado.");

            u.Nombre = dto.Nombre;
            u.Apellido = dto.Apellido;
            u.Telefono = dto.Telefono;
            u.Correo = dto.Correo;
            u.User = dto.Usuario;
            u.Direccion = dto.Direccion;
            u.Rol = dto.Rol;
            u.Activo = dto.Activo;

            if (!string.IsNullOrWhiteSpace(dto.Contrasena))
                u.ContrasenaHash = PasswordHasherUtil.Hash(dto.Contrasena); // <— AQUI

            await _context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE: api/usuarios/5
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var u = await _context.Usuarios.FindAsync(id);
            if (u == null) return NotFound();

            _context.Usuarios.Remove(u);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
