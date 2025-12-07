using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Models
{
    public class PedidoDetalle
    {
        public int Id { get; set; }

        public int PedidoId { get; set; }
        public Pedido Pedido { get; set; } = null!;

        public int MedicamentoId { get; set; }
        public Medicamento Medicamento { get; set; } = null!;

        public int Cantidad { get; set; }

        [Precision(18, 2)]
        [Column(TypeName = "decimal(18,2)")]
        public decimal PrecioUnitario { get; set; }

        [Precision(18, 2)]
        [Column(TypeName = "decimal(18,2)")]
        public decimal Subtotal { get; set; }

        public string MedicamentoNombre { get; set; } = string.Empty;
    }
}
