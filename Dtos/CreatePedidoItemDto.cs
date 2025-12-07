using System.ComponentModel.DataAnnotations;

namespace FarmaciaApi.DTOs
{
    public class CreatePedidoItemDto
    {
        [Required]
        public int MedicamentoId { get; set; }

        [Range(1, int.MaxValue)]
        public int Cantidad { get; set; }
    }
}
