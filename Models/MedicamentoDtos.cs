using System.ComponentModel.DataAnnotations;

namespace FarmaciaApi.DTOs
{
    public class CreateMedicamentoDto
    {
        [Required, MaxLength(150)]
        public string Nombre { get; set; } = string.Empty;

        [Range(0, int.MaxValue)]
        public int Cantidad { get; set; }

        [MaxLength(1000)]
        public string? Descripcion { get; set; }

        [MaxLength(2000)]
        public string? Beneficios { get; set; }

        [MaxLength(2000)]
        public string? Instrucciones { get; set; }

        [MaxLength(2000)]
        public string? Advertencias { get; set; }

        [MaxLength(60)]
        public string? Tipo { get; set; }

        [Range(typeof(decimal), "0", "79228162514264337593543950335")]
        public decimal Precio { get; set; }

        [MaxLength(300)]
        [Url(ErrorMessage = "FotoUrl debe ser una URL válida.")]
        public string? FotoUrl { get; set; }
    }

    public class UpdateMedicamentoDto
    {
        [Required, MaxLength(150)]
        public string Nombre { get; set; } = string.Empty;

        [Range(0, int.MaxValue)]
        public int Cantidad { get; set; }

        [MaxLength(1000)]
        public string? Descripcion { get; set; }

        [MaxLength(2000)]
        public string? Beneficios { get; set; }

        [MaxLength(2000)]
        public string? Instrucciones { get; set; }

        [MaxLength(2000)]
        public string? Advertencias { get; set; }

        [MaxLength(60)]
        public string? Tipo { get; set; }

        [Range(typeof(decimal), "0", "79228162514264337593543950335")]
        public decimal Precio { get; set; }

        [MaxLength(300)]
        [Url(ErrorMessage = "FotoUrl debe ser una URL válida.")]
        public string? FotoUrl { get; set; }
    }
}
