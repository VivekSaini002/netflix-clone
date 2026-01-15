package com.netflixClone.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netflixClone.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

}
