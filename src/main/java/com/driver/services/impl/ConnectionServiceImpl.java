package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();

        if(user.getConnected()==true){
            throw new Exception("connected");
        }
        //if same country
        if(user.getOriginalCountry().getCountryName().name().equalsIgnoreCase(countryName)){
            return user;
        }

        List<ServiceProvider> serviceProviders = user.getServiceProviderList();

        if(serviceProviders.size()==0){
            throw new Exception("Unable to connect");
        }

        //check if the serviceProviderList of user has the reqd country in the countrylist
        int smallest = Integer.MAX_VALUE;
        ServiceProvider provider=null;
        Country c=null;
        for(ServiceProvider serviceProvider:serviceProviders){

            List<Country> countries = serviceProvider.getCountryList();

            for(Country country:countries){
                if(country.getCountryName().name().equalsIgnoreCase(countryName) && serviceProvider.getId()<smallest){
                    smallest=serviceProvider.getId();
                    provider=serviceProvider;
                    c=country;
                }
            }
        }
        if(smallest==Integer.MAX_VALUE){
            throw new Exception("Unable to connect");
        }
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(provider);

        provider.getConnectionList().add(connection);
        user.getConnectionList().add(connection);
        user.setConnected(true);


        //maskedIp is "updatedCountryCode.serviceProviderId.userId"

        String countryCode = c.getCode();
        user.setMaskedIp(countryCode+"."+provider.getId()+"."+userId);

        userRepository2.save(user);
        serviceProviderRepository2.save(provider);

        return user;


    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(user.getConnected()==false){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);

        userRepository2.save(user);

        return user;

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        String currenctCountryOfReceiver="";
        //maskedIp is "updatedCountryCode.serviceProviderId.userId"
        if(receiver.getConnected()==true){
            String code = receiver.getMaskedIp().substring(0,3);
            for (CountryName countryName1 : CountryName.values()) {
                if (countryName1.toCode().equals(code)) {
                    currenctCountryOfReceiver= countryName1.name();
                }
            }
        }
        else{
            currenctCountryOfReceiver = receiver.getOriginalCountry().getCountryName().name(); //original country
        }

        if(sender.getOriginalCountry().getCountryName().name().equals(currenctCountryOfReceiver)) return sender;

        try {
            sender = connect(senderId,currenctCountryOfReceiver);
        }
        catch (Exception e){
            throw new Exception("Cannot establish communication");
        }
        if(sender.getConnected()==false) throw new Exception("Cannot establish communication");
        return sender;

    }
}
