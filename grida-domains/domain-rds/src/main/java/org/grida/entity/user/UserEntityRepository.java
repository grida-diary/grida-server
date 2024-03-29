package org.grida.entity.user;

import org.grida.domain.user.*;
import org.grida.exception.DomainRdsException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.grida.exception.DomainRdsErrorCode.USER_NOT_FOUND;

@Repository
@Transactional(readOnly = true)
public class UserEntityRepository implements UserRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public String saveAccount(
            UserAccount account,
            LocalDateTime lastActionAt
    ) {
        UserEntity userEntity = UserMapper.toUserEntity(account, lastActionAt);
        entityManager.persist(userEntity);

        return userEntity.getEmail();
    }

    @Override
    public User findByEmail(String email) {
        UserEntity userEntity = findUserEntityByEmail(email);
        return UserMapper.toUser(userEntity);
    }

    @Override
    public boolean existByEmail(String email) {
        return !entityManager.createQuery(
                        "select u.email from UserEntity u " +
                                "where u.email = :email " +
                                "order by u.id asc ",
                        String.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }

    @Override
    public UserAccount findAccountByEmail(String email) {
        return entityManager.createQuery(
                        "select new org.grida.domain.user.UserAccount(u.role, u.email, u.password, u.nickname) " +
                                "from UserEntity u " +
                                "where u.email = :email ",
                        UserAccount.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new DomainRdsException(USER_NOT_FOUND));
    }

    @Override
    public UserAppearance findAppearanceByEmail(String email) {
        return entityManager.createQuery(
                        "select new org.grida.domain.user.UserAppearance(u.age, u.gender, u.hairStyle, u.glasses) " +
                                "from UserEntity u " +
                                "where u.email = :email ",
                        UserAppearance.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new DomainRdsException(USER_NOT_FOUND));
    }

    @Override
    public UserRole findRoleByEmail(String email) {
        return entityManager.createQuery(
                        "select u.role from UserEntity u " +
                                "where u.email = :email",
                        UserRole.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new DomainRdsException(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public String modifyAppearance(String email, UserAppearance appearance) {
        UserEntity userEntity = findUserEntityByEmail(email);
        userEntity.modifyAppearance(appearance);

        return userEntity.getEmail();
    }

    private UserEntity findUserEntityByEmail(String email) {
        return entityManager.createQuery(
                        "select u from UserEntity u " +
                                "where u.email = :email ",
                        UserEntity.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new DomainRdsException(USER_NOT_FOUND));
    }
}
