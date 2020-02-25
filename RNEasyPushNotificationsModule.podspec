
Pod::Spec.new do |s|
  s.name         = "RNEasyPushNotificationsModule"
  s.version      = "1.1.8"
  s.summary      = "RNEasyPushNotificationsModule"
  s.description  = <<-DESC
                  RNEasyPushNotifications
                   DESC
  s.homepage     = "https://github.com/Blitz-Mobile-Apps/react-native-easy-push-notifications.git"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "alex.evans123@gmail.com" }
  s.platform     = :ios, "12.0"
  s.source       = { :git => "https://github.com/Blitz-Mobile-Apps/react-native-easy-push-notifications.git"}
  s.source_files  = "ios/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "Firebase/Messaging"
  #s.dependency "others"
  s.static_framework    = false
end

  
