@RestController
@RequestMapping("/product-composite")
@RequiredArgsConstructor
@Slf4j
public class ProductCompositeController {

    private final ProductClient productClient;
    private final ReviewClient reviewClient;
    private final RecommendationClient recommendationClient;
    private final RequestMetrics metrics; // ✅ injection du compteur

    @Value("${server.port}")
    private int port;

    // ============================
    // GET - requête composite
    // ============================
    @GetMapping("/{productId}")
    public Map<String, Object> getProductComposite(@PathVariable Long productId) {
        metrics.incrementGet(); // 🟢 compteur GET
        log.info("➡️ GET reçu pour produit {}", productId);
        // reste du code ...
        return Map.of("message", "GET OK", "productId", productId);
    }

    // ============================
    // POST - création composite
    // ============================
    @PostMapping
    public Map<String, Object> createComposite(@RequestBody Map<String, Object> body) {
        metrics.incrementPostPut(); // 🟠 compteur POST
        log.info("🆕 POST reçu");
        // logique création produit
        return Map.of("message", "POST OK");
    }

    // ============================
    // PUT - mise à jour composite
    // ============================
    @PutMapping("/{productId}")
    public Map<String, Object> updateComposite(@PathVariable Long productId, @RequestBody Map<String, Object> body) {
        metrics.incrementPostPut(); // 🟠 compteur PUT
        log.info("✏️ PUT reçu pour produit {}", productId);
        // logique mise à jour
        return Map.of("message", "PUT OK", "productId", productId);
    }
}
