package com.example.fintrack.security

import com.example.fintrack.user.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        try {

            val bearerToken = request.getHeader("Authorization")

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {

                val token = bearerToken.substring(7)

                if (jwtTokenProvider.validateToken(token)) {

                    val userId = jwtTokenProvider.getUserId(token)

                    val userDetails =
                        userDetailsService.loadUserById(userId)

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )

                    authentication.details =
                        WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: Exception) {
            println("JWT Error: ${e.message}")
        }


        filterChain.doFilter(request, response)
    }


    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-ui.html")
    }
}
