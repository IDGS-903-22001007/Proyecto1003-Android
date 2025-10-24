using FarmaciaApi.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.HttpOverrides;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

// CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowFrontend", policy =>
        policy.WithOrigins(
            "http://localhost:5173",
            "https://localhost:5173",
            "https://api-farmacia.ngrok.app",
            "https://farmacia.ngrok.app"
        )
        .AllowAnyMethod()
        .AllowAnyHeader()
    );
});

// EF Core
builder.Services.AddDbContext<FarmaciaContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("FarmaciaDb"))
);

// 👉 Swagger (SOLO ESTO; NO usar AddOpenApi/MapOpenApi)
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Farmacia API",
        Version = "v1",
        Description = "API del sistema de Farmacia"
    });
});

builder.Services.AddControllers();

var app = builder.Build();

// Proxy headers (ngrok)
app.UseForwardedHeaders(new ForwardedHeadersOptions
{
    ForwardedHeaders = ForwardedHeaders.XForwardedProto | ForwardedHeaders.XForwardedFor
});

app.UseCors("AllowFrontend");

// Archivos estáticos (fotos)
app.UseStaticFiles();

// 👉 Activa SIEMPRE Swagger (dev y prod)
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    // El JSON VIVE aquí →
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Farmacia API v1");
    c.RoutePrefix = "swagger"; // UI en /swagger
});

app.UseHttpsRedirection();

app.MapControllers();

// Diagnóstico opcional
app.MapGet("/ping", () => Results.Ok(new { ok = true, time = DateTime.UtcNow }));

app.Run();
