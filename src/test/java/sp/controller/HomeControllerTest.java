package sp.controller;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Home controller test
 *
 * @author Paul Kulitski
 */
public class HomeControllerTest {

    public HomeControllerTest() {
    }

    /**
     * Test of 'home' method, of class HomeController.
     */
    @Test
    public void testHome() {
        Model model = (Model) new ExtendedModelMap();
        HomeController homeController = new HomeController();
        String viewName = homeController.home(model);
        assertEquals(model.containsAttribute("view"), true);
        assertEquals("home", model.asMap().get("view"));
        assertEquals("home", viewName);
    }

    /**
     * Test of 'instruction' method, of class HomeController.
     */
    @Test
    public void testInstruction() {
        Model model = (Model) new ExtendedModelMap();
        HomeController homeController = new HomeController();
        String viewName = homeController.instruction(model);
        assertEquals("instruction", viewName);
    }

    /**
     * Test of 'contact' method, of class HomeController.
     */
    @Test
    public void testContact() {
        Model model = (Model) new ExtendedModelMap();
        HomeController homeController = new HomeController();
        String viewName = homeController.contact(model);
        assertEquals("contact", viewName);
    }

    /**
     * Test of 'about' method, of class HomeController.
     */
    @Test
    public void testAbout() {
        Model model = (Model) new ExtendedModelMap();
        HomeController homeController = new HomeController();
        String viewName = homeController.about(model);
        assertEquals("about", viewName);
    }
}