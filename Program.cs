using FarmaciaApi.Data;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

// ============ CORS ============
// Permite que el frontend de localhost acceda a la API
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowFrontend", policy =>
        policy.WithOrigins("http://localhost:5173", "https://farmacia.ngrok.app")
            .AllowAnyMethod()
            .AllowAnyHeader()
            .AllowCredentials());
});

// ============ EF Core ============
// Conexión a la base de datos
builder.Services.AddDbContext<FarmaciaContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("FarmaciaDb"))
);

// ============ AUTENTICACIÓN POR COOKIES ============
// Configuración de autenticación por cookies
builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
    .AddCookie(options =>
    {
        options.LoginPath = "/login"; // Ruta a la que redirigir cuando no haya sesión
        options.LogoutPath = "/logout"; // Ruta de logout
        options.ExpireTimeSpan = TimeSpan.FromMinutes(30); // Duración de la sesión
        options.SlidingExpiration = true; // Renueva la cookie en cada solicitud
    });

// ============ Swagger ============
// Configuración de Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Farmacia API",
        Version = "v1",
        Description = "API del sistema de Farmacia"
    });
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Token JWT en el header Authorization: Bearer {token}"
    });
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});

builder.Services.AddControllers();

var app = builder.Build();

// Middleware para CORS y autenticación
app.UseCors("AllowFrontend");     // CORS antes de auth / controllers
app.UseAuthentication();          // Autenticación basada en cookies
app.UseAuthorization();           // Necesario para la autorización

// Archivos estáticos (fotos)
app.UseStaticFiles();

// Swagger UI
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Farmacia API v1");
    c.RoutePrefix = "swagger";
});

app.MapControllers();  // Mapeo de rutas a los controladores

// Diagnóstico de salud
app.MapGet("/ping", () => Results.Ok(new { ok = true, time = DateTime.UtcNow }));

app.Run();
