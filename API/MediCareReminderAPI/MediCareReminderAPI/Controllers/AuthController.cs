using BCrypt.Net;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MediCareReminderAPI.Data;
using MediCareReminderAPI.Models;

namespace MediCareReminderAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public AuthController(ApplicationDbContext context)
        {
            _context = context;
        }

        // REGISTER
        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterRequest request)
        {
            // Validate full name
            if (string.IsNullOrWhiteSpace(request.FullName))
            {
                return BadRequest("Full name is required.");
            }

            // Validate email
            if (string.IsNullOrWhiteSpace(request.Email))
            {
                return BadRequest("Email is required.");
            }

            // Validate password
            if (string.IsNullOrWhiteSpace(request.Password))
            {
                return BadRequest("Password is required.");
            }

            // Clean email
            var email = request.Email.Trim().ToLower();

            // Check if email already exists
            var existingUser = await _context.Users
                .FirstOrDefaultAsync(u => u.Email == email);

            if (existingUser != null)
            {
                return BadRequest("An account with this email already exists.");
            }

            // Create new user
            var user = new User
            {
                FullName = request.FullName.Trim(),
                Email = email,

                // Securely hash the password
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.Password),

                CreatedAt = DateTime.UtcNow
            };

            // Save user
            _context.Users.Add(user);

            await _context.SaveChangesAsync();

            // Return successful response
            return Ok(new
            {
                message = "Registration successful.",
                userId = user.UserId,
                fullName = user.FullName,
                email = user.Email
            });
        }


        // LOGIN
        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequest request)
        {
            // Validate email
            if (string.IsNullOrWhiteSpace(request.Email))
            {
                return BadRequest("Email is required.");
            }

            // Validate password
            if (string.IsNullOrWhiteSpace(request.Password))
            {
                return BadRequest("Password is required.");
            }

            // Clean email
            var email = request.Email.Trim().ToLower();

            // Find user
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Email == email);

            // User doesn't exist
            if (user == null)
            {
                return Unauthorized("Invalid email or password.");
            }

            // Verify password against BCrypt hash
            bool passwordValid = BCrypt.Net.BCrypt.Verify(
                request.Password,
                user.PasswordHash);

            // Password is incorrect
            if (!passwordValid)
            {
                return Unauthorized("Invalid email or password.");
            }

            // Login successful
            return Ok(new
            {
                message = "Login successful.",
                userId = user.UserId,
                fullName = user.FullName,
                email = user.Email
            });
        }
    }
}