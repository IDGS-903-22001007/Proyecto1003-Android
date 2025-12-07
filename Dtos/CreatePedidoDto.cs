namespace FarmaciaApi.DTOs
{
    public class CreatePedidoDto
    {
        public string? ClienteNombre { get; set; }

        public string? DireccionEntrega { get; set; }

        public string? CoordenadasEntrega { get; set; }

        public decimal CostoEnvio { get; set; }

        public List<CreatePedidoItemDto> Items { get; set; } = new();
    }
}
