using FarmaciaApi.Models;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Data
{
    public class FarmaciaContext : DbContext
    {
        public FarmaciaContext(DbContextOptions<FarmaciaContext> options) : base(options) { }

        public DbSet<Usuario> Usuarios { get; set; } = null!;
        public DbSet<Medicamento> Medicamentos { get; set; } = null!;
        public DbSet<Proveedor> Proveedores { get; set; }
        public DbSet<Pedido> Pedidos { get; set; }
        public DbSet<PedidoDetalle> PedidoDetalles { get; set; }


        // 👇 NUEVO
        public DbSet<Cita> Citas { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Medicamentos: decimal exacto
            modelBuilder.Entity<Medicamento>()
                .Property(m => m.Precio)
                .HasColumnType("decimal(18,2)");

            // 👇 Configuración Citas
            modelBuilder.Entity<Cita>(e =>
            {
                e.ToTable("Citas");
                e.HasKey(x => x.IdCita);

                e.Property(x => x.TipoConsulta)
                    .HasMaxLength(100)
                    .IsRequired();

                e.Property(x => x.Estatus)
                    .HasMaxLength(1)
                    .HasDefaultValue("A");

                // Guardar segundos en 0: precisión a minuto
                e.Property(x => x.FechaHora)
                    .HasColumnType("datetime2(0)");

                e.Property(x => x.DuracionMin)
                    .HasDefaultValue(30);

                // Índice único filtrado: impide dos citas ACTIVAS en el mismo minuto
                e.HasIndex(x => x.FechaHora)
                 .HasDatabaseName("IX_Citas_FechaHora_Activas")
                 .IsUnique()
                 .HasFilter("[Estatus] = 'A'");
            });
        }
    }
}
