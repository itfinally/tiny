package top.itfinally.security.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itfinally.security.component.AbstractValidationImageComponent;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.springframework.util.StringUtils.isEmpty;

@RestController
@RequestMapping( "/verifies" )
public class ImageVerifyController {
  private final Logger logger = LoggerFactory.getLogger( getClass() );
  private AbstractValidationImageComponent imageVerifyComponent;

  @Autowired
  public ImageVerifyController setImageVerifyComponent( AbstractValidationImageComponent imageVerifyComponent ) {
    this.imageVerifyComponent = imageVerifyComponent;
    return this;
  }

  @GetMapping( "/get_valid_image/{account}/{now}" )
  public void getValidImage( @PathVariable( "account" ) String account, @PathVariable("now") long now,
                             HttpServletResponse response ) throws IOException {

    if ( isEmpty( account ) ) {
      return;
    }

    BufferedImage stream = imageVerifyComponent.createImage( account );

    response.setContentType( "image/png" );
    response.setHeader( "Cache-Control", "no-cache" );
    ImageIO.write( stream, "PNG", response.getOutputStream() );
  }
}
