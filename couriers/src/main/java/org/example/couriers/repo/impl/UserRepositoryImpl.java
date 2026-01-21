package org.example.couriers.repo.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.example.couriers.entitys.User;
import org.example.couriers.repo.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {
    @PersistenceContext
    EntityManager em;

    public UserRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByIdOrEmailOrPhone(UUID id, String email, String phone) {
        try {
            return Optional.of(em.createQuery(
                    "SELECT u FROM User u " +
                            "WHERE u.id = :id or u.phone = :phone or u.email = :email", User.class)
                    .setParameter("id", id)
                    .setParameter("phone", phone)
                    .setParameter("email", email)
                    .getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }
}
