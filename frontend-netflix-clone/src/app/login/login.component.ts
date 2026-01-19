import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthServiceService } from '../shared/services/auth-service.service';
import { Route, Router } from '@angular/router';
import { NotificationServiceService } from '../shared/services/notification-service.service';
import { ErrorHandlerServiceService } from '../shared/services/error-handler-service.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  hide = true;
  loginForm!: FormGroup;
  loading = false;
  showResendLink = false;
  userEmail = '';

  constructor(
    private fb: FormBuilder,
    private authServce: AuthServiceService,
    private router: Router,
    private notification: NotificationServiceService,
    private errorHandleService: ErrorHandlerServiceService,
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    if (this.authServce.isLoggedIn()) {
      this.authServce.redirectBasedOnRole();
    }
  }

  submit() {
    this.loading = true;
    const formData = this.loginForm.value;
    const authData = {
      email: formData.email?.trim().toLowerCase(),
      password: formData.password,
    };
    this.authServce.login(authData).subscribe({
      next: (response: any) => {
        this.loading = false;
        this.authServce.redirectBasedOnRole();
      },
      error: (err) => {
        this.loading = false;
        const errorMsg =
          err.error?.error || 'Login failed. Please check your credentials.';
        if (err.status === 403 && errorMsg.toLowerCase().includes('verify')) {
          this.showResendLink = true;
          this.userEmail = this.loginForm.value.email;
        } else {
          this.showResendLink = false;
        }
        this.notification.error(errorMsg);
        console.error('Login error : ', err);
      },
    });
  }

  resendVerification() {
    if (!this.userEmail) {
      this.notification.error('Please enter your email address');
      return;
    }

    this.showResendLink = false;
    this.loading = true;
    this.authServce.resendVerificationEmail(this.userEmail).subscribe({
      next: (response: any) => {
        this.loading = false;
        this.notification.success(
          response.message ||
            'verification email sent! Please check your inbox.',
        );
      },
      error: (err) => {
        this.loading = false;
        this.errorHandleService.handle(
          err,
          'Failed to send verification email. Please try again.',
        );
      },
    });
  }

  forgot(){
    this.router.navigate(['/forgot-password']);
  }





}
