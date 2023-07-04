package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();

        int price = 0;

        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            price += 500+(subscriptionEntryDto.getNoOfScreensRequired()*200);
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)) {
            price += 800+(subscriptionEntryDto.getNoOfScreensRequired()*250);
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE)) {
            price += 1000+(subscriptionEntryDto.getNoOfScreensRequired()*350);
        }
        subscription.setTotalAmountPaid(price);
        subscription.setUser(user);
        user.setSubscription(subscription);
        userRepository.save(user);
        return price;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        int prevPrice = user.getSubscription().getTotalAmountPaid();
        int currPrice = 0;

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        } else if (user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC)) {
            currPrice += 800+(user.getSubscription().getNoOfScreensSubscribed()*250);
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
        }
        else{
            currPrice += 1000+(user.getSubscription().getNoOfScreensSubscribed()*350);
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
        }
        subscriptionRepository.save(user.getSubscription());
        return currPrice-prevPrice;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int revenue = 0;
        for(Subscription subscriptions:subscriptionList){
            revenue += subscriptions.getTotalAmountPaid();
        }
        return revenue;
    }

}
