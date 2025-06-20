package aero.sita.mgt.auth_service.Controllers;

import aero.sita.mgt.auth_service.Schemas.DTO.PermissionRequest;
import aero.sita.mgt.auth_service.Services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Permission Controller", description = "Endpoints for managing user permissions")
@RestController
@RequestMapping("/api/v2/permission")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // JWT authentication required
public class PermissionController {

    private final ApplicationService applicationService;

    @Operation(summary = "Create a new permission")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission created successfully", content = @Content(schema = @Schema(implementation = PermissionRequest.class))),
            @ApiResponse(responseCode = "400", description = "Permission already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PermissionRequest> createPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(applicationService.createPermission(request));
    }

    @Operation(summary = "Get all permissions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of permissions", content = @Content(schema = @Schema(implementation = PermissionRequest.class)))
    })
    @GetMapping
    public ResponseEntity<List<PermissionRequest>> getAllPermissions() {
        return ResponseEntity.ok(applicationService.getAllPermissions());
    }

    @Operation(summary = "Get a permission by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission found", content = @Content(schema = @Schema(implementation = PermissionRequest.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @GetMapping("/{name}")
    public ResponseEntity<PermissionRequest> getPermission(@PathVariable String name) {
        return ResponseEntity.ok(applicationService.getPermissionByName(name));
    }

    @Operation(summary = "Update a permission by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission updated successfully", content = @Content(schema = @Schema(implementation = PermissionRequest.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @PutMapping("/{name}")
    public ResponseEntity<PermissionRequest> updatePermission(@PathVariable String name, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(applicationService.updatePermission(name, request));
    }

    @Operation(summary = "Soft delete a permission by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission soft deleted"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deletePermission(@PathVariable String name) {
        applicationService.deletePermission(name);
        return ResponseEntity.ok("Permission deleted successfully.");
    }
}