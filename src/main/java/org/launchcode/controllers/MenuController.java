package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu menu,
                                     Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}")
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);

        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}")
    public String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);

        model.addAttribute("form", new AddMenuItemForm(menu, cheeseDao.findAll()));
        model.addAttribute("title", "Add Cheese to " + menu.getName());

        return "menu/add-item";

    }

    @RequestMapping(value = "add-item")
    public String addItem(@ModelAttribute @Valid AddMenuItemForm form,
                          Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Cheese to Menu");
            return "menu/add-item";
        }

        int cheeseId = form.getCheeseId();
        int menuId = form.getMenuId();

        Menu theMenu = menuDao.findOne(menuId);
        Cheese theCheese = cheeseDao.findOne(cheeseId);

        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:view/" + theMenu.getId();
    }

}
