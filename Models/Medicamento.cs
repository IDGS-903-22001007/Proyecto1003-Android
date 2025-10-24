using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Models
{
    [Index(nameof(Nombre))]
    public class Medicamento
    {
        public int Id { get; set; }

        [Required, MaxLength(150)]
        public string Nombre { get; set; } = string.Empty;

        [Range(0, int.MaxValue)]
        public int Cantidad { get; set; }

        [MaxLength(1000)]
        public string? Descripcion { get; set; }

        // Texto largo
        [MaxLength(2000)]
        public string? Beneficios { get; set; }

        [MaxLength(2000)]
        public string? Instrucciones { get; set; }

        [MaxLength(2000)]
        public string? Advertencias { get; set; }

        [MaxLength(60)]
        public string? Tipo { get; set; } // tableta, jarabe, etc.

        [Precision(18, 2)]
        [Column(TypeName = "decimal(18,2)")]
        public decimal Precio { get; set; }

        // 👉 Solo URL (no archivos)
        [MaxLength(300)]
        public string? FotoUrl { get; set; }

        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;
        public bool Activo { get; set; } = true;
    }
}
