using FarmaciaApi.Models;
using Microsoft.EntityFrameworkCore;

namespace FarmaciaApi.Data
{
    public class FarmaciaContext : DbContext
    {
        public FarmaciaContext(DbContextOptions<FarmaciaContext> options) : base(options) { }

        public DbSet<Usuario> Usuarios { get; set; } = null!;

        // 👇 NUEVO
        public DbSet<Medicamento> Medicamentos { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Si quieres asegurar el tipo decimal:
            modelBuilder.Entity<Medicamento>()
                .Property(m => m.Precio)
                .HasColumnType("decimal(18,2)");
        }
    }
}
