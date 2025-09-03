package bo.edu.ucb.ms.sales.client;

import bo.edu.ucb.ms.sales.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @GetMapping("/api/warehouse/products/{productId}")
    ResponseEntity<ProductDto> getProduct(@PathVariable("productId") Integer productId);

    @PostMapping("/api/warehouse/products/{productId}/stock/check")
    ResponseEntity<Map<String, Object>> checkStockAvailability(
            @PathVariable("productId") Integer productId,
            @RequestParam("requiredQuantity") Integer requiredQuantity);

    @PostMapping("/api/warehouse/products/{productId}/stock/reserve")
    ResponseEntity<ProductDto> reserveStock(
            @PathVariable("productId") Integer productId,
            @RequestParam("quantity") Integer quantity);

    @PostMapping("/api/warehouse/products/{productId}/stock/release")
    ResponseEntity<ProductDto> releaseStock(
            @PathVariable("productId") Integer productId,
            @RequestParam("quantity") Integer quantity);

    @PutMapping("/api/warehouse/products/{productId}/stock")
    ResponseEntity<ProductDto> updateStock(
            @PathVariable("productId") Integer productId,
            @RequestBody ProductDto productDto);
}