using FarmaciaApi.Data;
using FarmaciaApi.Models;
using FarmaciaApi.Security;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;
using System.Threading.Tasks;

namespace FarmaciaApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly FarmaciaContext _context;

        public AuthController(FarmaciaContext context)
        {
            _context = context;
        }

        // Método de login
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

            
            var claims = new[]
            {
        new Claim(ClaimTypes.Name, usr.User),
        new Claim(ClaimTypes.NameIdentifier, usr.Id.ToString()),
        new Claim(ClaimTypes.Role, usr.Rol) 
    };

            var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
            var principal = new ClaimsPrincipal(identity);

            
            await HttpContext.SignInAsync(CookieAuthenticationDefaults.AuthenticationScheme, principal);

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

        
        [HttpPost("logout")]
        public async Task<IActionResult> Logout()
        {
            await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
            return Ok(new { message = "Logout exitoso" });
        }
    }

    
    public class LoginRequest
    {
        public string User { get; set; }
        public string Contrasena { get; set; }
    }
}
