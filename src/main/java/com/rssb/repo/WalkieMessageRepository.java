package com.rssb.repo;

import com.rssb.model.WalkieMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkieMessageRepository extends JpaRepository<WalkieMessage, Long> {}
