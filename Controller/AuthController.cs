using FarmaciaApi.Data;
using FarmaciaApi.Security; // <— AÑADE ESTO
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;

namespace FarmaciaApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly FarmaciaContext _context;
        public AuthController(FarmaciaContext context) => _context = context;

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest req)
        {
            if (string.IsNullOrWhiteSpace(req.User) || string.IsNullOrWhiteSpace(req.Contrasena))
                return BadRequest("Usuario y contraseña son requeridos.");

            var usr = await _context.Usuarios
                .FirstOrDefaultAsync(u => u.User == req.User && u.Activo);

            if (usr == null) return Unauthorized("Usuario no encontrado o inactivo.");

            bool ok = PasswordHasherUtil.Verify(req.Contrasena, usr.ContrasenaHash);
            if (!ok) return Unauthorized("Contraseña incorrecta.");

            return Ok(new
            {
                message = "Login exitoso",
                userId = usr.Id,
                nombre = usr.Nombre,
                apellido = usr.Apellido,
                user = usr.User,
                rol = usr.Rol
            });
        }
    }

    public class LoginRequest
    {
        public string User { get; set; } = "";
        public string Contrasena { get; set; } = "";
    }
}
