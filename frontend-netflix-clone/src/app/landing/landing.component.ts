import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: false,
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css',
})
export class LandingComponent {
  landingForm!: FormGroup;

  year = new Date().getFullYear();

  constructor(
    private fb: FormBuilder,
    private router: Router,
  ) {
    this.landingForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  login() {
    this.router.navigate(['/login']);
  }

  getStarted() {
    // console.log('Form submitted', this.landingForm.value);
    this.router.navigate(['/signup'], {
      queryParams: { email: this.landingForm.value.email },
    });
  }

  reasons = [
    {
      title: 'Enjoy on your TV.',
      text: 'Watch on Smart TVs, Playstation, Xbox, players and more.',
      icon: 'tv',
    },
    {
      title: 'Download your shows to watch offline.',
      text: 'Save your favourites easily and always have something to watch.',
      icon: 'file_download',
    },
    {
      title: 'Watch everywhere.',
      text: 'Stream unlimited movies and TV shows on your phone, tablet, laptop, and TV without paying more.',
      icon: 'devices',
    },
    {
      title: 'Create profiles for kids.',
      text: 'Send kids on adventures with their favourite characters in a space made just for them—free with your membership.',
      icon: 'face',
    },
  ];

  faqs = [
    {
      question: 'What is PluseScreen?',
      answer:
        "PulseScreen is streaming service that offers a wide variety of award-winning TV shows, movies, anime, documentaries, and more on thousands of internet-connected devices. You can watch as much as you want, whenever you want, without a single commercial – all for one low monthly price. There's always something new to discover, and new TV shows and movies are added every week!",
    },
    {
      question: 'How much does PluseScreen cost?',
      answer:
        'Plans start at rupees 149 per month. No extra costs, no contracts.',
    },
    {
      question: 'Where can I watch?',
      answer:
        "Watch anywhere, anytime, on an unlimited number of devices. Sign in with your PluseScreen account to watch instantly on the web at PluseScreen.com from your personal computer or on any internet-connected device that offers the PluseScreen app, including smart TVs, smartphones, tablets, streaming media players and game consoles. You can also download your favourite shows with the iOS, Android, or Windows 10 app. Use downloads to watch while you're on the go and without an internet connection. Take PluseScreen with you anywhere.",
    },
    {
      question: 'How do I cancel?',
      answer:
        'You can cancel your PluseScreen subscription at any time. There are no cancellation fees – start or stop your account anytime.',
    },
    {
      question: 'What can I watch on PluseScreen?',
      answer:
        'PluseScreen has an extensive library of feature films, documentaries, TV shows, anime, award-winning PluseScreen originals, and more. Watch as much as you want, anytime you want.',
    },
    {
      question: 'Is PluseScreen good for kids?',
      answer:
        "The PluseScreen Kids experience is included in your membership to give parents control while kids enjoy family-friendly TV shows and movies in their own space. Kids profiles come with PIN-protected parental controls that let you restrict the maturity rating of content kids can watch and block specific titles you don't want kids to see.",
    },
  ];
}
