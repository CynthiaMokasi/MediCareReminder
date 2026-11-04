
using BCrypt.Net;
using Google.Apis.Auth;
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
        private readonly IConfiguration _configuration;

        public AuthController(
            ApplicationDbContext context,
            IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }

        // REGISTER WITH EMAIL AND PASSWORD
        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.FullName))
                return BadRequest("Full name is required.");

            if (string.IsNullOrWhiteSpace(request.Email))
                return BadRequest("Email is required.");

            if (string.IsNullOrWhiteSpace(request.Password))
                return BadRequest("Password is required.");

            var email = request.Email.Trim().ToLowerInvariant();

            var existingUser = await _context.Users
                .FirstOrDefaultAsync(u => u.Email == email);

            if (existingUser != null)
                return BadRequest("An account with this email already exists.");

            var user = new User
            {
                FullName = request.FullName.Trim(),
                Email = email,
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(request.Password),
                CreatedAt = DateTime.UtcNow
            };

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                message = "Registration successful.",
                userId = user.UserId,
                fullName = user.FullName,
                email = user.Email
            });
        }

        // LOGIN WITH EMAIL AND PASSWORD
        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Email))
                return BadRequest("Email is required.");

            if (string.IsNullOrWhiteSpace(request.Password))
                return BadRequest("Password is required.");

            var email = request.Email.Trim().ToLowerInvariant();

            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Email == email);

            if (user == null)
                return Unauthorized("Invalid email or password.");

            bool passwordValid = BCrypt.Net.BCrypt.Verify(
                request.Password,
                user.PasswordHash);

            if (!passwordValid)
                return Unauthorized("Invalid email or password.");

            return Ok(new
            {
                message = "Login successful.",
                userId = user.UserId,
                fullName = user.FullName,
                email = user.Email
            });
        }

        // LOGIN WITH GOOGLE
        [HttpPost("google")]
        public async Task<IActionResult> GoogleLogin(
            [FromBody] GoogleLoginRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.IdToken))
                return BadRequest("Google ID token is required.");

            var googleClientId =
                _configuration["Google:ClientId"];

            if (string.IsNullOrWhiteSpace(googleClientId))
                return StatusCode(
                    500,
                    "Google Client ID is not configured.");

            GoogleJsonWebSignature.Payload payload;

            try
            {
                payload = await GoogleJsonWebSignature.ValidateAsync(
                    request.IdToken,
                    new GoogleJsonWebSignature.ValidationSettings
                    {
                        Audience = new[] { googleClientId }
                    });
            }
            catch (Exception)
            {
                return Unauthorized("Invalid Google ID token.");
            }

            if (string.IsNullOrWhiteSpace(payload.Subject) ||
                string.IsNullOrWhiteSpace(payload.Email) ||
                payload.EmailVerified != true)
            {
                return Unauthorized(
                    "Google account email could not be verified.");
            }

            var email = payload.Email.Trim().ToLowerInvariant();

            var user = await _context.Users
                .FirstOrDefaultAsync(u =>
                    u.GoogleSubject == payload.Subject);

            if (user == null)
            {
                user = await _context.Users
                    .FirstOrDefaultAsync(u => u.Email == email);

                if (user != null)
                {
                    // Do not automatically link a password account.
                    // Explicit account linking requires an authenticated,
                    // separately authorised linking flow.
                    return Conflict(
                        "An account with this email already exists. " +
                        "Please log in using your existing method first.");
                }

                user = new User
                {
                    FullName = string.IsNullOrWhiteSpace(payload.Name)
                        ? email
                        : payload.Name,
                    Email = email,
                    GoogleSubject = payload.Subject,
                    PasswordHash = BCrypt.Net.BCrypt.HashPassword(
                        Guid.NewGuid().ToString("N") +
                        Guid.NewGuid().ToString("N")),
                    CreatedAt = DateTime.UtcNow
                };

                _context.Users.Add(user);
                await _context.SaveChangesAsync();
            }

            return Ok(new
            {
                message = "Google login successful.",
                userId = user.UserId,
                fullName = user.FullName,
                email = user.Email
            });
        }
    }

    public class GoogleLoginRequest
    {
        public string IdToken { get; set; } = string.Empty;
    }
}
