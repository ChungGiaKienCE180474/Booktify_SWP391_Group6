package shop.controller.client;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.domain.VppItem;
import shop.service.VppItemService;

@Controller
public class VppImageController {

    private final VppItemService vppItemService;

    public VppImageController(VppItemService vppItemService) {
        this.vppItemService = vppItemService;
    }

    @GetMapping({
            "/uploads/vpp/{id}/image",
            "/customer/vpp-image/{id}/image"
    })
    public ResponseEntity<byte[]> showImage(@PathVariable Long id) {
        VppItem item = vppItemService.getEntityById(id);

        byte[] imageData = item.getImageData();

        if (imageData == null || imageData.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String contentType = item.getImageContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                .body(imageData);
    }
}