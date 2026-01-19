import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthServiceService } from '../shared/services/auth-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationServiceService } from '../shared/services/notification-service.service';
import { ErrorHandlerServiceService } from '../shared/services/error-handler-service.service';

@Component({
  selector: 'app-signup',
  standalone: false,
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
})
export class SignupComponent implements OnInit{
  hidePassword = true;
  hideConfirmPassword = true;
  signupForm!: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthServiceService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: NotificationServiceService,
    private errorHandlerService: ErrorHandlerServiceService,
  ) {
    this.signupForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: [
        '',
        [
          Validators.required,
          this.authService.passwordMatchValidator('password'),
        ],
      ],
    });
  }

  ngOnInit(): void {
      // TODO
      const email = this.route.snapshot.queryParams['email'];
      if(email){
        this.signupForm.patchValue({email: email});
        console.log(email);
      }
  }

  submit(){
    this.loading = true;
    const formData = this.signupForm.value;
    const data = {
      email: formData.email?.trim().toLowerCase(),
      password: formData.password,
      fullName: formData.fullName
    };

    this.authService.signup(data).subscribe({
      next: (response: any) => {
        this.loading = false;
        this.notification.success(response?.message);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        this.errorHandlerService.handle(err, 'Registration failed. Please try again.');
      }
    });
  }



}
