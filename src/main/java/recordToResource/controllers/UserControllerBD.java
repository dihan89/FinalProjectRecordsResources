package recordToResource.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import recordToResource.daoAndServiceUtils.Generator.GeneratorFromFile;
import recordToResource.daoAndServiceUtils.dao.RecordDao;
import recordToResource.daoAndServiceUtils.dao.ResourceDao;
import recordToResource.daoAndServiceUtils.dao.UserDao;
import recordToResource.exceptions.DateBeforeNowException;
import recordToResource.model.Record;
import recordToResource.model.Resource;
import recordToResource.model.User;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ListIterator;

@Controller
@RequestMapping("/bd")
public class UserControllerBD {
    public static boolean first = false;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private RecordDao recordDao;
    @Autowired
    private GeneratorFromFile generatorFromFile;

    private List<Resource> resourceList;
    private List<Record> recordList;
    private List<Record> allYourRecords;
    private Record record;
    private User yourUser;
    private String message = "";

    @PostConstruct
    private void init() {
        resourceList = resourceDao.findAll();
    }

    @GetMapping("/")
    public String start() {
        message = "";
        return "index";
    }

    @GetMapping("/userResourceDate")
    public String userResourceDate(Model model) {
        record = new Record();
        model.addAttribute("resources", resourceList);
        model.addAttribute("message", message);
        return "records/userResourceDate";
    }

    @GetMapping("/finpage")
    public String finPage() {
        return "records/finpage";
    }

    @GetMapping("/messageNotRecords")
    private String messageNotRecords() {
        return "records/messageNotRecords";
    }

    @GetMapping("/userdata")
    public String userdata(Model model) {
        model.addAttribute("message", message);
        return "records/userdata";
    }

    @GetMapping("/times")
    private String chooseTime(Model model) {
        model.addAttribute("message", message);
        model.addAttribute("recordList", recordList);
        return "records/times";
    }

    @GetMapping("/allYourRecords")
    private String allYourRecords(Model model) {
        model.addAttribute("recordList2", allYourRecords);
        model.addAttribute("message", message);
        return "records/allYourRecords";
    }

    @GetMapping("/notFoundEmptyRecords")
    private String notFoundEmptyRecords() {
        return "records/notFoundEmptyRecords";
    }

    @PostMapping("/delete")
    private String deleteRecord(Model model, @RequestParam(value = "recordString") String recNum) {
        int n = 0;
        try {
            n = Integer.parseInt(recNum);
            if ((n < 1) || (n > allYourRecords.size()))
                throw new IllegalArgumentException("The number is not correct!");
        } catch (Exception exc) {
            message = "Enter a correct number!";
            return "redirect:allYourRecords";
        }
        recordDao.deleteUserFromRecord(allYourRecords.get(n - 1));
        message = "";
        return "redirect:finPage";
    }

    @PostMapping("/add")
    private String addRecord(Model model, @RequestParam(value = "name") String name,
                             @RequestParam(value = "surname") String surname,
                             @RequestParam(value = "phone") String phone,
                             @RequestParam(value = "res") String resNumber,
                             @RequestParam(value = "date") String dateString) {
        Date date;
        try {
            date = Date.valueOf(dateString);
            if (date.before(Date.valueOf(LocalDate.now()))) {
                throw new DateBeforeNowException(dateString);
            }
        } catch (Exception exc) {
            message = "The date is not correct! ";
            return "redirect:userResourceDate";
        }

        int resourceNo = 0;
        try {
            resourceNo = Integer.parseInt(resNumber);
            if (resourceNo < 1 || resourceNo > resourceList.size())
                throw new IllegalArgumentException(String.format("%s%d", "index of resource: ", resourceNo));
        } catch (Exception exc) {
            message = "Resource's number is not correct!";
            return "redirect:userResourceDate";
        }

        yourUser = userDao.get(name, surname, phone);
        if (yourUser == null) {
            message = "User data is not correct! ";
            return "redirect:userResourceDate";
        }
        if (yourUser.getId() == null) {
            message = "Oh! Maybe, there is exists another user " +
                    "with the same phone number!";
            return "redirect:userResourceDate";
        }

        record.setUser(yourUser);
        record.setDate(date);
        record.setResource(resourceList.get(resourceNo - 1));
        recordList =
                recordDao.findEmptyRecordsByResourceAndDate(record.getResource(), date);
        message = "";
        if (recordList == null || recordList.isEmpty()) {
            return "redirect:notFoundEmptyRecords";
        }
        return "redirect:times";
    }


    @PostMapping("/find")
    private String find(Model model, @RequestParam(value = "name") String name,
                        @RequestParam(value = "surname") String surname,
                        @RequestParam(value = "phone") String phone) {
        yourUser = userDao.find(name, surname, phone);
        if (yourUser == null) {
            message = "User data is not correct or user does not exist!";
            return "redirect:userdata";
        }
        message = "";
        allYourRecords = recordDao.findRecordsByUser(yourUser);
        if (allYourRecords.isEmpty())
            return "redirect:messageNotRecords";
        return "redirect:allYourRecords";
    }

    @PostMapping("/setTime")
    private String setTime(@RequestParam(value = "timeString") String nTime) {
        int timeIntervalNo = 0;
        try {
            timeIntervalNo = Integer.parseInt(nTime);
            if (timeIntervalNo < 1 || timeIntervalNo > recordList.size())
                throw new IllegalArgumentException("Number of empty time " +
                        "must be in the correct diapason");
            timeIntervalNo--;
        } catch (Exception exc) {
            message = "Please enter the number of time!";
            return "redirect:times";
        }
        if (recordList.get(timeIntervalNo).getUser() != null) {
            message = "Sorry, this time is busy!";
            return "redirect:times";
        }
        record.setTimeStart(recordList.get(timeIntervalNo).getTimeStart());
        recordDao.addUser(yourUser, recordList.get(timeIntervalNo));
        message = "";
        return "redirect:finpage";
    }

    public String getAllResources() {
        StringBuilder stringBuilder = new StringBuilder("RESOURCES:");
        ListIterator<Resource> iter = resourceList.listIterator();
        int ind = 0;
        while (iter.hasNext()) {
            stringBuilder.append("    ").append(++ind).append(". ")
                    .append(iter.next().getName())
                    .append(" ");
        }
        return stringBuilder.toString();
    }
}



