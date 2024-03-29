package LeiYang.controller;

import LeiYang.entity.Users;
import LeiYang.service.CloudWatchService;
import LeiYang.service.MyUserDetailsService;
import LeiYang.util.Bycrypt;
import LeiYang.util.EmailVerify;
import LeiYang.util.ExceptionMessage;
import LeiYang.entity.UserVo;
import LeiYang.service.UserService;

import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.security.Principal;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    //test url
    @RequestMapping("/health")
    public ExceptionMessage responseHealthWithoutZ(){
        long startTime = System.currentTimeMillis();
        long responseTime = System.currentTimeMillis() - startTime;
        statsDClient.incrementCounter("health.count");
        statsDClient.recordExecutionTime("health.time", responseTime);
        cloudWatchService.sendCustomMetric("Health", 1, responseTime);
        logger.info("Health");
        return new ExceptionMessage().success();
    }

    @RequestMapping("/healthz")
    public ExceptionMessage responseHealth(){
        long startTime = System.currentTimeMillis();
        long responseTime = System.currentTimeMillis() - startTime;
        statsDClient.incrementCounter("healthz.count");
        statsDClient.recordExecutionTime("healthz.time", responseTime);
        cloudWatchService.sendCustomMetric("Healthz", 1, responseTime);
        logger.info("Healthz");
        return new ExceptionMessage().success();
    }

    @Autowired
    private StatsDClient statsDClient;
    @Resource
    private UserService userService;
    @Autowired
    private CloudWatchService cloudWatchService;
    //依赖注入，添加CloudWatchServic，记录Controller

    @PostMapping("/v1/user")
    public ExceptionMessage add(@RequestBody UserVo userVo){
        long startTime = System.currentTimeMillis();
        if(EmailVerify.isValidEmail(userVo.getEmail())){
            if(userService.find(userVo.getEmail()) != null){
                long responseTime = System.currentTimeMillis() - startTime;
                statsDClient.incrementCounter("user.created.failed.count");
                statsDClient.recordExecutionTime("user.created.failed.time", responseTime);
                cloudWatchService.sendCustomMetric("UserCreationFailed", 1, responseTime);
                logger.info("UserCreationFailed Failed");
                return new ExceptionMessage().fail();
            }
            else{
                String password = Bycrypt.encryptPassword(userVo.getPassword());
                //System.out.println(password);
                Users users = new Users(userVo.getFirstName(), userVo.getLastName(), userVo.getEmail(),password);
                userService.save(users);
                long responseTime = System.currentTimeMillis() - startTime;
                statsDClient.incrementCounter("user.created.succeed.count");
                statsDClient.recordExecutionTime("user.created.succeed.time", responseTime);
                cloudWatchService.sendCustomMetric("UserCreated", 1, responseTime);
                logger.info("UserCreated succeed");
                return new ExceptionMessage().success();
            }
        }
        long responseTime = System.currentTimeMillis() - startTime;
        statsDClient.incrementCounter("invalidemail.count");
        statsDClient.recordExecutionTime("invalidemail.time", responseTime);
        cloudWatchService.sendCustomMetric("InvalidEmail", 1, responseTime);
        logger.info("InvalidEmail");
        return new ExceptionMessage().fail();
    }

    @PutMapping("/v1/user/{id}")
    public ExceptionMessage update(@RequestBody UserVo user,@PathVariable Long id){
        long startTime = System.currentTimeMillis();
        //添加认证是否为本人
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        if(!userName.equals(userService.get(id).getEmail_address()))
        {
            long responseTime = System.currentTimeMillis() - startTime;
            statsDClient.incrementCounter("user.updated.failed.count");
            statsDClient.recordExecutionTime("user.updated.failed.time", responseTime);
            cloudWatchService.sendCustomMetric("UserUpdateFailed", 1, responseTime);
            logger.info("UserUpdate Failed");
            return new ExceptionMessage().fail();
        }
        String password = Bycrypt.encryptPassword(user.getPassword());
        userService.update(user.getFirstName(), user.getLastName(), password, id);
        long responseTime = System.currentTimeMillis() - startTime;
        statsDClient.incrementCounter("user.updated.succeed.count");
        statsDClient.recordExecutionTime("user.updated.succeed.time", responseTime);
        cloudWatchService.sendCustomMetric("UserUpdated", 1, responseTime);
        logger.info("UserUpdated Succeed");
        return new ExceptionMessage().success();
    }

    @GetMapping("/v1/user/{id}")
    public Users getUser(@PathVariable Long id){
        long startTime = System.currentTimeMillis();
        Users users = userService.get(id);
        if (users == null) {
            long responseTime = System.currentTimeMillis() - startTime;
            statsDClient.incrementCounter("user.not.found.count");
            statsDClient.recordExecutionTime("user.not.found.time", responseTime);
            cloudWatchService.sendCustomMetric("UserNotFound", 1, responseTime);
            logger.info("UserNotFound");
        } else {
            long responseTime = System.currentTimeMillis() - startTime;
            statsDClient.incrementCounter("user.not.found.count");
            statsDClient.recordExecutionTime("user.not.found.time", responseTime);
            cloudWatchService.sendCustomMetric("UserFound", 1, responseTime);
            logger.info("UserFound Succeed");
        }
        return users;
    }

}
