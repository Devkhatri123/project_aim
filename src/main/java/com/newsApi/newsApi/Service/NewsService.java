package com.newsApi.newsApi.Service;

import com.newsApi.newsApi.exception.OperationNotAllowed;
import com.newsApi.newsApi.exception.SubscriptionExpired;
import com.newsApi.newsApi.model.Subscription;
import com.newsApi.newsApi.model.UserDetails;
import com.newsApi.newsApi.repository.NewsRepo;
import com.newsApi.newsApi.model.News;
import com.newsApi.newsApi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NewsService {
    private final NewsRepo newsRepo;
    private final UserRepo userRepo;

    @Autowired
    public NewsService(final NewsRepo newsRepo, final UserRepo userRepo){
        this.newsRepo = newsRepo;
        this.userRepo = userRepo;
    }

    public void addNews(List<News> news){
      newsRepo.saveAll(news);
    }

    // Get all news
    public List<News> getAllNews(Integer pageNumber, Integer pageSize) {
        return newsRepo.getNewsByDebunk(false,PageRequest.of(pageNumber,pageSize)).getContent();
    }
    // Get all news headings
    public List<Object> getAllNewsHeadings(boolean is_debunk,Integer days) {
         return newsRepo.getNewsHeadings(is_debunk,days);
    }
    // Get Debunk news Headings
    public List<Object> getAllDeBunkNewsHeadings(boolean is_debunk,Integer days){
      return newsRepo.getNewsHeadings(is_debunk,days);
    }
    // Get category wise news
    public List<News> getNewsCategoryWise(String category, Integer pageNumber, Integer pageSize) {
        return newsRepo.findByCategory(category, PageRequest.of(pageNumber, pageSize));
    }
    public List<News> getDeBunkNews(Integer pageNumber, Integer pageSize) throws OperationNotAllowed, SubscriptionExpired {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Subscription userSubscription = userDetails.getUser().getMySubscription();
       if(userSubscription != null && userDetails.getUser().getRole().equals("SUBSCRIBER")) {
           if(!userSubscription.getExpiresOn().isBefore(LocalDate.now())) {
               return newsRepo.getNewsByDebunk(true, PageRequest.of(pageNumber, pageSize)).getContent();
           } else {
               userDetails.getUser().setMySubscription(null);
               userDetails.getUser().setRole("GUEST");
               userRepo.save(userDetails.getUser());
               throw new SubscriptionExpired("Your subscription has expired");
           }
       }
        throw new OperationNotAllowed("Your subscription has expired");
    }
}
