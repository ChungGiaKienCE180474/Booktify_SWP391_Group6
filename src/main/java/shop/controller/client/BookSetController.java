package shop.controller.client;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.BookSet;
import shop.service.BookSetService;

@Controller
@RequestMapping("/book-sets")
public class BookSetController {

    private final BookSetService bookSetService;

    public BookSetController(BookSetService bookSetService) {
        this.bookSetService = bookSetService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String grade,
            Model model) {

        // Keep supporting old ?grade= links (e.g. GRADE_1) as tag filter.
        String selectedTag = (tag != null && !tag.isBlank()) ? tag : grade;

        List<BookSet> sets = bookSetService.searchActive(q, selectedTag);
        Map<Long, Integer> availableMap = new LinkedHashMap<>();
        Map<Long, String> setPriceMap = new LinkedHashMap<>();
        Map<Long, String> retailMap = new LinkedHashMap<>();
        Map<Long, String> tagLabelMap = new LinkedHashMap<>();
        Map<Long, Boolean> discountedMap = new LinkedHashMap<>();
        Map<String, String> setTagLabels = new LinkedHashMap<>();

        for (BookSet set : sets) {
            BigDecimal setPrice = set.getSetPrice() == null ? BigDecimal.ZERO : set.getSetPrice();
            BigDecimal retail = bookSetService.getRetailTotal(set);
            availableMap.put(set.getId(), bookSetService.getAvailableSetQuantity(set));
            setPriceMap.put(set.getId(), bookSetService.formatMoney(setPrice));
            retailMap.put(set.getId(), bookSetService.formatMoney(retail));
            tagLabelMap.put(set.getId(), bookSetService.tagLabel(set.getGradeLevel()));
            discountedMap.put(set.getId(), retail.compareTo(setPrice) > 0);
        }

        for (String rawTag : bookSetService.listActiveTags()) {
            setTagLabels.put(rawTag, bookSetService.tagLabel(rawTag));
        }

        model.addAttribute("bookSets", sets);
        model.addAttribute("q", q);
        model.addAttribute("tag", selectedTag);
        model.addAttribute("tagLabel", bookSetService.tagLabel(selectedTag));
        model.addAttribute("availableMap", availableMap);
        model.addAttribute("setPriceMap", setPriceMap);
        model.addAttribute("retailMap", retailMap);
        model.addAttribute("tagLabelMap", tagLabelMap);
        model.addAttribute("discountedMap", discountedMap);
        model.addAttribute("setTagLabels", setTagLabels);
        return "book-set/list";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            BookSet bookSet = bookSetService.getByIdWithItems(id);
            if (!bookSet.isActive()) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "This book set is no longer available."
                );
                return "redirect:/book-sets";
            }

            BigDecimal setPrice = bookSet.getSetPrice() == null
                    ? BigDecimal.ZERO
                    : bookSet.getSetPrice();
            BigDecimal retail = bookSetService.getRetailTotal(bookSet);

            model.addAttribute("bookSet", bookSet);
            model.addAttribute("availableSets", bookSetService.getAvailableSetQuantity(bookSet));
            model.addAttribute("setPriceFormatted", bookSetService.formatMoney(setPrice));
            model.addAttribute("retailTotalFormatted", bookSetService.formatMoney(retail));
            model.addAttribute("discounted", retail.compareTo(setPrice) > 0);
            model.addAttribute("tagLabel", bookSetService.tagLabel(bookSet.getGradeLevel()));
            return "book-set/detail";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/book-sets";
        }
    }
}
