using System;
using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Models
{
    [Index(nameof(User), IsUnique = true)]
    [Index(nameof(Correo), IsUnique = true)]
    public class Usuario
    {
        public int Id { get; set; }

        [Required, MaxLength(80)]
        public string Nombre { get; set; } = string.Empty;

        [Required, MaxLength(80)]
        public string Apellido { get; set; } = string.Empty;

        [Required, MaxLength(20)]
        public string Telefono { get; set; } = string.Empty;

        [Required, MaxLength(120), EmailAddress]
        public string Correo { get; set; } = string.Empty;

        // Username
        [Required, MaxLength(50)]
        public string User { get; set; } = string.Empty;

        [Required, MaxLength(200)]
        public string Direccion { get; set; } = string.Empty;

        // Guardar hash, no texto plano
        [Required]
        public string ContrasenaHash { get; set; } = string.Empty;

        // Para tus módulos (admin/user). Puedes usar enum si prefieres.
        [Required, MaxLength(20)]
        public string Rol { get; set; } = "user";

        public bool Activo { get; set; } = true;
        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;
    }
}
