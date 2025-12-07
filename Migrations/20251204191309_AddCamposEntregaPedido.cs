using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace FarmaciaApi.Migrations
{
    /// <inheritdoc />
    public partial class AddCamposEntregaPedido : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "CoordenadasEntrega",
                table: "Pedidos",
                type: "nvarchar(100)",
                maxLength: 100,
                nullable: true);

            migrationBuilder.AddColumn<decimal>(
                name: "CostoEnvio",
                table: "Pedidos",
                type: "decimal(18,2)",
                precision: 18,
                scale: 2,
                nullable: false,
                defaultValue: 0m);

            migrationBuilder.AddColumn<string>(
                name: "DireccionEntrega",
                table: "Pedidos",
                type: "nvarchar(250)",
                maxLength: 250,
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "CoordenadasEntrega",
                table: "Pedidos");

            migrationBuilder.DropColumn(
                name: "CostoEnvio",
                table: "Pedidos");

            migrationBuilder.DropColumn(
                name: "DireccionEntrega",
                table: "Pedidos");
        }
    }
}
