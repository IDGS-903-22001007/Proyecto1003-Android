using System.ComponentModel.DataAnnotations;

namespace FarmaciaApi.Dtos
{
    public class UpdateProveedorDto
    {
        [Required, MaxLength(150)]
        public string Nombre { get; set; } = string.Empty;

        [MaxLength(150)]
        public string? Contacto { get; set; }

        [MaxLength(20)]
        public string? Telefono { get; set; }

        [EmailAddress, MaxLength(150)]
        public string? Email { get; set; }

        [MaxLength(300)]
        public string? Direccion { get; set; }

        [MaxLength(50)]
        public string? Rfc { get; set; }

        public bool Activo { get; set; }
    }
}
