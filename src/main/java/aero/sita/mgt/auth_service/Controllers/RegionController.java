package aero.sita.mgt.auth_service.Controllers;

import aero.sita.mgt.auth_service.Schemas.DTO.RegionRequest;
import aero.sita.mgt.auth_service.Schemas.DTO.RegionResponse;
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

@Tag(name = "Region Controller", description = "Controller for managing regions")
@RestController
@RequestMapping("/api/v2/region")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // Requer autenticação JWT para todos os endpoints
public class RegionController {

    private final ApplicationService applicationService;

    @Operation(summary = "Create a new region")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Region created successfully", content = @Content(schema = @Schema(implementation = RegionRequest.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RegionRequest> createRegion(@RequestBody RegionRequest request) {
        return ResponseEntity.ok(applicationService.createRegion(request));
    }

    @Operation(summary = "Get all regions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all regions", content = @Content(schema = @Schema(implementation = RegionRequest.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })

    @GetMapping
    public ResponseEntity<List<RegionResponse>> getAllRegions() {
        return ResponseEntity.ok(applicationService.getAllRegions());
    }

    @Operation(summary = "Get a region by code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Region found", content = @Content(schema = @Schema(implementation = RegionRequest.class))),
            @ApiResponse(responseCode = "404", description = "Region not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{code}")
    public ResponseEntity<RegionRequest> getRegion(@PathVariable String code) {
        return ResponseEntity.ok(applicationService.getRegionByCode(code));
    }

    @Operation(summary = "Update a region by code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Region updated successfully", content = @Content(schema = @Schema(implementation = RegionRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{code}")
    public ResponseEntity<RegionRequest> updateRegion(@PathVariable String code, @RequestBody RegionRequest request) {
        return ResponseEntity.ok(applicationService.updateRegion(code, request));
    }

    @Operation(summary = "Soft delete a region (sets enabled=false)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Region deleted successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Region not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{code}")
    public ResponseEntity<String> deleteRegion(@PathVariable String code) {
        applicationService.deleteRegion(code);
        return ResponseEntity.ok("Region deleted successfully.");
    }
}