using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations.Schema;

namespace FarmaciaApi.Models
{
    public class Pedido
    {
        public int Id { get; set; }

        [MaxLength(150)]
        public string? ClienteNombre { get; set; }

        // 🔹 Nueva: dirección donde se entregará
        [MaxLength(250)]
        public string? DireccionEntrega { get; set; }

        // 🔹 Nueva: coordenadas (ej. "21.12345,-101.23456")
        [MaxLength(100)]
        public string? CoordenadasEntrega { get; set; }

        public DateTime FechaCreacion { get; set; } = DateTime.UtcNow;

        [MaxLength(30)]
        public string Estado { get; set; } = "Pendiente";

        // 🔹 Nuevo: costo de envío
        [Precision(18, 2)]
        [Column(TypeName = "decimal(18,2)")]
        public decimal CostoEnvio { get; set; }

        [Precision(18, 2)]
        [Column(TypeName = "decimal(18,2)")]
        public decimal Total { get; set; }

        public List<PedidoDetalle> Detalles { get; set; } = new();
    }
}
