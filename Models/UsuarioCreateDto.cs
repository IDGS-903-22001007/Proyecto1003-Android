namespace FarmaciaApi.Models
{
    public class UsuarioCreateDto
    {
        public string Nombre { get; set; } = "";
        public string Apellido { get; set; } = "";
        public string Telefono { get; set; } = "";
        public string Correo { get; set; } = "";
        public string Usuario { get; set; } = "";
        public string Direccion { get; set; } = "";
        public string Contrasena { get; set; } = "";
        public string Rol { get; set; } = "user";
    }

    public class UsuarioUpdateDto
    {
        public string Nombre { get; set; } = "";
        public string Apellido { get; set; } = "";
        public string Telefono { get; set; } = "";
        public string Correo { get; set; } = "";
        public string Usuario { get; set; } = "";
        public string Direccion { get; set; } = "";
        public string? Contrasena { get; set; }  // opcional
        public string Rol { get; set; } = "user";
        public bool Activo { get; set; } = true;
    }

    public class LoginRequest
    {
        public string Usuario { get; set; } = "";
        public string Contrasena { get; set; } = "";
    }
}
