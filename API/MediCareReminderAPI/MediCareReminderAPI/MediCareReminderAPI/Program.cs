using Microsoft.EntityFrameworkCore;
using MediCareReminderAPI.Data;
using MediCareReminderAPI.Services;
using Microsoft.Azure.Cosmos;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseSqlServer(
        builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddSingleton<BlobStorageService>();

// Register Azure Cosmos DB
builder.Services.AddSingleton<CosmosClient>(serviceProvider =>
{
    var configuration = serviceProvider.GetRequiredService<IConfiguration>();

    var endpoint = configuration["CosmosDb:Endpoint"];
    var key = configuration["CosmosDb:Key"];

    if (string.IsNullOrWhiteSpace(endpoint) ||
        string.IsNullOrWhiteSpace(key))
    {
        throw new InvalidOperationException(
            "CosmosDb Endpoint or Key is missing. Check User Secrets.");
    }

    return new CosmosClient(endpoint, key);
});

var app = builder.Build();

// Enable Swagger in Azure and during local testing
app.UseSwagger();
app.UseSwaggerUI();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();