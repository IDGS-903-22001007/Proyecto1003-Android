using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Models
{
    [Index(nameof(Nombre))]
    public class Proveedor
    {
        public int Id { get; set; }

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

        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;
        public bool Activo { get; set; } = true;

        // Relación con medicamentos
        public List<Medicamento> Medicamentos { get; set; } = new();

        // 🔹 AQUI guardaremos TODOS los pedidos a este proveedor en JSON
        public string? HistorialPedidosJson { get; set; }
    }
}
