package shop.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations(
                        "/resources/",
                        "file:src/main/webapp/resources/"
                );

        registry.addResourceHandler("/css/**")
                .addResourceLocations(
                        "/resources/css/",
                        "file:src/main/webapp/resources/css/"
                );

        registry.addResourceHandler("/js/**")
                .addResourceLocations(
                        "/resources/js/",
                        "file:src/main/webapp/resources/js/"
                );

        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "/resources/images/",
                        "file:src/main/webapp/resources/images/"
                );

        registry.addResourceHandler("/client/**")
                .addResourceLocations(
                        "/resources/client/",
                        "file:src/main/webapp/resources/client/"
                );

        String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads")
                .toUri()
                .toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}