package ua.epam.mishchenko.ticketbooking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMongo {
    private String id;
    private String name;
    private String email;
    private UserAccountMongo userAccount;
}
