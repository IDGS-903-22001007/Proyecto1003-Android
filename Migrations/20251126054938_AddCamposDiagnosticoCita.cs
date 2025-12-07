using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace FarmaciaApi.Migrations
{
    /// <inheritdoc />
    public partial class AddCamposDiagnosticoCita : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "Diagnostico",
                table: "Citas",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Medicamentos",
                table: "Citas",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Observaciones",
                table: "Citas",
                type: "nvarchar(max)",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Diagnostico",
                table: "Citas");

            migrationBuilder.DropColumn(
                name: "Medicamentos",
                table: "Citas");

            migrationBuilder.DropColumn(
                name: "Observaciones",
                table: "Citas");
        }
    }
}
