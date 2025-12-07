public class Cita
{
    public int IdCita { get; set; }
    public int IdPaciente { get; set; }
    public string NombrePaciente { get; set; }
    public DateTime FechaHora { get; set; }
    public string TipoConsulta { get; set; } = null!;
    public string? Notas { get; set; }
    public string Estatus { get; set; } = "A";
    public int DuracionMin { get; set; } = 30;

    // 🔹 NUEVOS CAMPOS
    public string? Observaciones { get; set; }
    public string? Diagnostico { get; set; }
    public string? Medicamentos { get; set; }
}
