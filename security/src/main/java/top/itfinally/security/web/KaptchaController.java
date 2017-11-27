package top.itfinally.security.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itfinally.security.service.KaptchaService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class KaptchaController {
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private KaptchaService kaptchaService;

    @Autowired
    public KaptchaController setKaptchaService( KaptchaService kaptchaService ) {
        this.kaptchaService = kaptchaService;
        return this;
    }

    @GetMapping( "/get_valid_image/{account}/{random}" )
    public void getValidImage(
            @PathVariable( "account" ) String account,
            @PathVariable( "random" ) Object random,
            HttpServletResponse response
    ) {
        BufferedImage stream = kaptchaService.getImage( account );
        try {
            response.setContentType( "image/png" );
            response.setHeader( "Cache-Control", "no-cache" );
            ImageIO.write( stream, "PNG", response.getOutputStream() );

        } catch ( IOException ex ) {
            logger.error( "Failed to write valid image to output stream.", ex );
        }
    }
}
