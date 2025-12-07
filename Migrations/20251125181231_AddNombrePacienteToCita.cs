using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace FarmaciaApi.Migrations
{
    /// <inheritdoc />
    public partial class AddNombrePacienteToCita : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "NombrePaciente",
                table: "Citas",
                type: "nvarchar(max)",
                nullable: false,
                defaultValue: "");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "NombrePaciente",
                table: "Citas");
        }
    }
}
