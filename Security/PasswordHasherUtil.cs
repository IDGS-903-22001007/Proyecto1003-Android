using System;
using System.Security.Cryptography;
using Microsoft.AspNetCore.Cryptography.KeyDerivation;

namespace FarmaciaApi.Security
{
    public static class PasswordHasherUtil
    {
        // Devuelve cadena con formato: PBKDF2|iteraciones|saltBase64|hashBase64
        public static string Hash(string password)
        {
            if (string.IsNullOrWhiteSpace(password))
                throw new ArgumentException("Password vacío.");

            // Salt de 16 bytes
            byte[] salt = RandomNumberGenerator.GetBytes(16);

            // Iteraciones recomendadas (ajústalo si quieres)
            int iterations = 100_000;

            // Hash de 32 bytes
            byte[] hash = KeyDerivation.Pbkdf2(
                password: password,
                salt: salt,
                prf: KeyDerivationPrf.HMACSHA256,
                iterationCount: iterations,
                numBytesRequested: 32);

            return $"PBKDF2|{iterations}|{Convert.ToBase64String(salt)}|{Convert.ToBase64String(hash)}";
        }

        public static bool Verify(string password, string stored)
        {
            if (string.IsNullOrWhiteSpace(stored)) return false;

            var parts = stored.Split('|');
            if (parts.Length != 4 || parts[0] != "PBKDF2") return false;

            int iterations = int.Parse(parts[1]);
            byte[] salt = Convert.FromBase64String(parts[2]);
            byte[] expectedHash = Convert.FromBase64String(parts[3]);

            byte[] computedHash = KeyDerivation.Pbkdf2(
                password: password,
                salt: salt,
                prf: KeyDerivationPrf.HMACSHA256,
                iterationCount: iterations,
                numBytesRequested: expectedHash.Length);

            // Comparación en tiempo constante
            return CryptographicOperations.FixedTimeEquals(expectedHash, computedHash);
        }
    }
}
