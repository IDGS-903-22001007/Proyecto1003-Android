namespace FarmaciaApi.Dtos
{
    public class CitaCreateDto
    {
        public DateTime FechaHora { get; set; }
        public string TipoConsulta { get; set; } = null!;
        public string NombrePaciente { get; set; } = null!;
        public string? Notas { get; set; }
        public int? DuracionMin { get; set; }

        // Opcionalmente ya los puedes recibir desde el inicio
        public string? Observaciones { get; set; }
        public string? Diagnostico { get; set; }
        public string? Medicamentos { get; set; }
    }


    public class CitaUpdateDto
    {
        public string TipoConsulta { get; set; } = null!;
        public string? Notas { get; set; }
        public string? Estatus { get; set; }

        // 🔹 NUEVOS CAMPOS
        public string? Observaciones { get; set; }
        public string? Diagnostico { get; set; }
        public string? Medicamentos { get; set; }
    }

}
