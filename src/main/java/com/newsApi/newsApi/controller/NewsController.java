package com.newsApi.newsApi.controller;

import com.newsApi.newsApi.Service.NewsService;
import com.newsApi.newsApi.dto.ErrorMessage;
import com.newsApi.newsApi.dto.Response;
import com.newsApi.newsApi.exception.OperationNotAllowed;
import com.newsApi.newsApi.exception.SubscriptionExpired;
import com.newsApi.newsApi.model.News;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(final NewsService newsService){
        this.newsService = newsService;
    }


    @PostMapping("/up")
    public String serverUpStatus(){
        return "Server is up";
    }

    @PostMapping("/news")
    public ResponseEntity<?> addNews(@RequestBody List<News> news){
       try {
           Response newsCreateJsonResponse = new Response();
           newsCreateJsonResponse.setData("News data added successfully");
           newsCreateJsonResponse.setStatusCode(201);
           newsService.addNews(news);
           return ResponseEntity.status(HttpStatus.CREATED).body(newsCreateJsonResponse);
       } catch (RuntimeException e) {
           ErrorMessage errorMessage = new ErrorMessage();
           errorMessage.setErrorMessage("Internal Server error");
           errorMessage.setStatusCode(500);
           log.error("Internal Server error: {}", e.getMessage());
           return ResponseEntity.internalServerError().body(errorMessage);
       }
    }
    @GetMapping("/AllNews")
    public ResponseEntity<?> getAllNews(@RequestParam Integer pageNumber, @RequestParam Integer pageSize){
        Response response = new Response();
        try {
            response.setData(newsService.getAllNews(pageNumber, pageSize));
            response.setStatusCode(200);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Internal Server error: {}", e.getMessage());
            response.setData("Internal Server error");
            response.setStatusCode(500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @GetMapping("/news/debunk")
    public ResponseEntity<?> getDebunkNews(@RequestParam Integer pageNumber, @RequestParam Integer pageSize){
      try {
          Response response = new Response();
          response.setData(newsService.getDeBunkNews(pageNumber, pageSize));
          response.setStatusCode(200);
          return ResponseEntity.status(HttpStatus.OK).body(response);
      } catch (RuntimeException e) {
          Response errorMessage = new Response();
          if(e instanceof SubscriptionExpired || e instanceof OperationNotAllowed){
              errorMessage.setData(e.getMessage());
              errorMessage.setStatusCode(400);
          }else {
              errorMessage.setData("Internal Server error");
              errorMessage.setStatusCode(500);
          }
          return ResponseEntity.status(HttpStatusCode.valueOf(errorMessage.getStatusCode())).body(errorMessage);
      }
    }
    @GetMapping("/headings")
    public ResponseEntity<?> getAllNewsHeadings(@RequestParam boolean is_debunk, @RequestParam Integer days){
        Response response = new Response();
        response.setData(newsService.getAllNewsHeadings(is_debunk,days));
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/debunk/headings")
    public ResponseEntity<?> getAllDebunkNewsHeadings(@RequestParam boolean is_debunk, @RequestParam Integer days){
        Response response = new Response();
        response.setData(newsService.getAllDeBunkNewsHeadings(is_debunk,days));
        response.setStatusCode(200);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/news/{category}")
    public ResponseEntity<?> getNewsCategoryWise(@PathVariable String category, @RequestParam Integer pageNumber, @RequestParam Integer pageSize){
        Response response = new Response();
        try {
            response.setData(newsService.getNewsCategoryWise(category,pageNumber,pageSize));
            response.setStatusCode(200);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.setData("Internal Server error");
            response.setStatusCode(500);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
