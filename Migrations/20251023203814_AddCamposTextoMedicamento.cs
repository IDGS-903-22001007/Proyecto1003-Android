using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace FarmaciaApi.Migrations
{
    /// <inheritdoc />
    public partial class AddCamposTextoMedicamento : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "Advertencias",
                table: "Medicamentos",
                type: "nvarchar(2000)",
                maxLength: 2000,
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Beneficios",
                table: "Medicamentos",
                type: "nvarchar(2000)",
                maxLength: 2000,
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Instrucciones",
                table: "Medicamentos",
                type: "nvarchar(2000)",
                maxLength: 2000,
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Advertencias",
                table: "Medicamentos");

            migrationBuilder.DropColumn(
                name: "Beneficios",
                table: "Medicamentos");

            migrationBuilder.DropColumn(
                name: "Instrucciones",
                table: "Medicamentos");
        }
    }
}
