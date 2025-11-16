package com.carrefour.infrastructure.api;

import com.carrefour.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final JwtService jwtService;

    @Value("${security.auth.username}")
    private String configuredUser;

    @Value("${security.auth.password}")
    private String configuredPassword;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Operation(
            summary = "Authenticate user and generate JWT token",
            description = """
                Authenticates a user using the configured username/password and 
                returns a JWT token if the credentials are valid.
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Example credentials",
                                            value = """
                                                {
                                                  "username": "user",
                                                  "password": "password-user"
                                                }
                                                """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful. JWT token returned.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Token example",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/login")
    public Mono<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login attempt received for username: '{}'", request.username());

        if (!request.username().equals(configuredUser) ||
                !request.password().equals(configuredPassword)) {
            log.warn("Authentication failed: invalid credentials for username '{}'",
                    request.username());
            return Mono.error(new RuntimeException("Invalid credentials"));
        }

        String token = jwtService.generateToken(request.username());
        log.info("Authentication successful for username '{}'. JWT token generated.",
                request.username());
        return Mono.just(new TokenResponse(token));
    }

    public record LoginRequest(@Schema(description = "Username used for authentication", example = "admin")
                               @NotBlank(message = "username must not be blank")
                               @Size(min = 3, max = 50, message = "username length must be between 3 and 50 characters") String username,
                               @Schema(description = "Password used for authentication", example = "carrefour123")
                               @NotBlank(message = "password must not be blank")
                               @Size(min = 6, max = 100, message = "password length must be between 6 and 100 characters")
                               String password) {}
    public record TokenResponse(@Schema(description = "Generated JWT token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token) {}
}
